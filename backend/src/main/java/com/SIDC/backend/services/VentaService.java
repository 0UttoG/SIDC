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

    // ¡Aquí está la magia! El constructor ahora recibe los 4 servicios
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

            if (promoOpt.isPresent()) {
                BigDecimal descuento = precioUnitario.multiply(
                        promoOpt.get().getPorcentajeDescuento().divide(new BigDecimal(100), 2, java.math.RoundingMode.HALF_UP)
                );
            }

            detalle.setPrecioUnitario(precioUnitario);
            detalle.setVenta(venta);
            venta.getDetalles().add(detalle);

            BigDecimal subtotal = precioUnitario.multiply(new BigDecimal(dto.cantidad()));
            totalCalculado = totalCalculado.add(subtotal);
        }

        venta.setTotal(totalCalculado);
        Venta ventaGuardada = ventaRepository.save(venta);

        emailService.enviarCorreo(
                "cliente@correo.com",
                "Comprobante de Venta SIDC",
                "Su compra por un total de $" + ventaGuardada.getTotal() + " ha sido procesada exitosamente."
        );

        return ventaGuardada;
    }
}