package com.SIDC.backend.services;

import com.SIDC.backend.dto.VentaDetalleRequestDTO;
import com.SIDC.backend.dto.VentaRequestDTO;
import com.SIDC.backend.entities.Cliente;
import com.SIDC.backend.entities.Promocion;
import com.SIDC.backend.entities.Venta;
import com.SIDC.backend.entities.VentaDetalle;
import com.SIDC.backend.repositories.ClienteRepository;
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

    public VentaService(VentaRepository ventaRepository, EmailService emailService,
                        ClienteRepository clienteRepository, PromocionRepository promocionRepository) {
        this.ventaRepository = ventaRepository;
        this.emailService = emailService;
        this.clienteRepository = clienteRepository;
        this.promocionRepository = promocionRepository;
    }

    @Transactional
    public Venta procesarVenta(VentaRequestDTO request) {
        Cliente cliente = clienteRepository.findById(request.idCliente())
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

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

            // Lógica de promociones (se mantiene en Java)
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

        // ⚠️ AL GUARDAR, SE ACTIVAN LOS TRIGGERS DE POSTGRESQL:
        // 1. trg_antes_insertar_venta: Valida rutas y límite de crédito.
        // 2. trg_antes_insertar_detalle: Valida stock y vencimiento.
        // 3. trg_despues_insertar_detalle: Resta el stock automáticamente.
        Venta ventaGuardada = ventaRepository.save(venta);

        // Enviar correo (opcional, no bloquea la venta)
        try {
            emailService.enviarCorreo(
                    cliente.getCorreo() != null ? cliente.getCorreo() : "info@sidc.com",
                    "Comprobante de Venta SIDC",
                    "Su compra por $" + ventaGuardada.getTotal() + " ha sido procesada exitosamente."
            );
        } catch (Exception e) {
            System.err.println("Error al enviar correo: " + e.getMessage());
        }

        return ventaGuardada;
    }
}