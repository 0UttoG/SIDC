// Archivo: src/main/java/com/SIDC/backend/services/VentaService.java
package com.SIDC.backend.services;

import com.SIDC.backend.dto.VentaDetalleRequestDTO;
import com.SIDC.backend.dto.VentaRequestDTO;
import com.SIDC.backend.entities.Cliente;
import com.SIDC.backend.entities.Promocion;
import com.SIDC.backend.entities.Venta;
import com.SIDC.backend.entities.VentaDetalle;
import com.SIDC.backend.repositories.ClienteRepository;
import com.SIDC.backend.repositories.ProductoRepository;
import com.SIDC.backend.repositories.PromocionRepository;
import com.SIDC.backend.repositories.VentaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

@Service
public class VentaService {

    private final VentaRepository ventaRepository;
    private final EmailService emailService;
    private final ClienteRepository clienteRepository;
    private final PromocionRepository promocionRepository;
    private final ProductoRepository productoRepository;
    private final PdfReporteService pdfReporteService;

    public VentaService(VentaRepository ventaRepository,
                        EmailService emailService,
                        ClienteRepository clienteRepository,
                        PromocionRepository promocionRepository,
                        ProductoRepository productoRepository,
                        PdfReporteService pdfReporteService) {
        this.ventaRepository = ventaRepository;
        this.emailService = emailService;
        this.clienteRepository = clienteRepository;
        this.promocionRepository = promocionRepository;
        this.productoRepository = productoRepository;
        this.pdfReporteService = pdfReporteService;
    }

    @Transactional
    public Venta procesarVenta(VentaRequestDTO request) {
        Cliente cliente = clienteRepository.findById(request.idCliente())
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

        // Buscamos si hay promoción activa para este canal
        Optional<Promocion> promoOpt = promocionRepository.buscarPromocionActiva(cliente.getCanal(), LocalDate.now());

        Venta venta = new Venta();
        venta.setIdCliente(request.idCliente());
        venta.setIdVendedor(request.idVendedor());
        venta.setIdRuta(request.idRuta());
        venta.setEsCredito(request.esCredito());

        BigDecimal totalCalculado = BigDecimal.ZERO;

        for (VentaDetalleRequestDTO dto : request.detalles()) {
            VentaDetalle detalle = new VentaDetalle();
            detalle.setIdBodega(dto.idBodega());
            detalle.setIdProducto(dto.idProducto());
            detalle.setIdLote(dto.idLote());
            detalle.setCantidad(dto.cantidad());

            BigDecimal precioUnitario = dto.precioUnitario();

            // Lógica de promociones
            if (promoOpt.isPresent()) {
                BigDecimal descuento = precioUnitario.multiply(
                        promoOpt.get().getPorcentajeDescuento().divide(new BigDecimal(100), 2, java.math.RoundingMode.HALF_UP)
                );
                precioUnitario = precioUnitario.subtract(descuento);
            }

            detalle.setPrecioUnitario(precioUnitario);
            detalle.setVenta(venta);
            venta.getDetalles().add(detalle);

            BigDecimal subtotal = precioUnitario.multiply(new BigDecimal(dto.cantidad()));
            totalCalculado = totalCalculado.add(subtotal);
        }

        venta.setTotal(totalCalculado);

        Venta ventaGuardada = ventaRepository.save(venta);

        // 👇 Generar PDF y enviar correo con la factura
        try {
            java.util.List<String> nombresProductos = new java.util.ArrayList<>();
            for (VentaDetalleRequestDTO dto : request.detalles()) {
                com.SIDC.backend.entities.Producto p = productoRepository.findById(dto.idProducto()).orElse(null);
                nombresProductos.add(p != null ? p.getNombre() : "Producto #" + dto.idProducto());
            }

            // 👇 Le pasamos la promoción al generador de PDF (si existe, sino manda null)
            byte[] facturaPdf = pdfReporteService.generarFacturaVentaPdf(
                    cliente, ventaGuardada, nombresProductos, promoOpt.orElse(null)
            );

            if (facturaPdf != null) {
                String condicion = ventaGuardada.getEsCredito() ? "al CRÉDITO" : "de CONTADO";

                // 👇 Si hubo descuento, lo incluimos en el texto del correo
                String textoDescuento = promoOpt.isPresent() ?
                        " (Incluye un ahorro del " + promoOpt.get().getPorcentajeDescuento() + "% por promoción activa)." : ".";

                String mensajeCuerpo = "Hola " + cliente.getNombre() + ",\n\n" +
                        "Gracias por su compra. Adjuntamos la factura detallada por un total de $" + ventaGuardada.getTotal() + textoDescuento + "\n" +
                        "Condición de la venta: " + condicion + ".\n\n" +
                        "Atentamente,\nEquipo SIDC";

                String correoDestino = (cliente.getCorreo() != null && !cliente.getCorreo().isEmpty())
                        ? cliente.getCorreo() : "noreply@sidc.com";

                emailService.enviarFacturaConAdjunto(
                        correoDestino,
                        "Factura de Compra N° " + ventaGuardada.getId() + " - SIDC",
                        mensajeCuerpo,
                        facturaPdf
                );
            }
        } catch (Exception e) {
            System.err.println("Error en la generación/envío de la factura PDF: " + e.getMessage());
        }

        return ventaGuardada;
    }
}