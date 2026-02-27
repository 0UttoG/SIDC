package com.SIDC.backend.services;

import com.SIDC.backend.dto.VentaDetalleRequestDTO;
import com.SIDC.backend.dto.VentaRequestDTO;
import com.SIDC.backend.entities.Venta;
import com.SIDC.backend.entities.VentaDetalle;
import com.SIDC.backend.repositories.VentaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class VentaService {

    private final VentaRepository ventaRepository;
    private final EmailService emailService;

    // Inyección de dependencias por constructor
    public VentaService(VentaRepository ventaRepository, EmailService emailService) {
        this.ventaRepository = ventaRepository;
        this.emailService = emailService;
    }

    @Transactional
    public Venta procesarVenta(VentaRequestDTO request) {
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
            detalle.setPrecioUnitario(dto.precioUnitario());

            // Relación bidireccional
            detalle.setVenta(venta);
            venta.getDetalles().add(detalle);

            // Calculamos el total en memoria para guardarlo en la cabecera
            BigDecimal subtotal = dto.precioUnitario().multiply(new BigDecimal(dto.cantidad()));
            totalCalculado = totalCalculado.add(subtotal);
        }

        venta.setTotal(totalCalculado);

        // Al hacer save(), JPA inserta la venta y en cascada los detalles.
        // Si hay violación de stock o crédito, salta la excepción aquí y hace ROLLBACK.
        Venta ventaGuardada = ventaRepository.save(venta);

        // Si llegamos a esta línea, la transacción en PostgreSQL fue un éxito.
        // (Asumo que tu EmailService tiene un método así, ajusta el nombre si es necesario)
        emailService.enviarCorreo(
                "cliente@correo.com", // Aquí luego puedes extraer el correo real del cliente
                "Comprobante de Venta SIDC",
                "Su compra por un total de $" + ventaGuardada.getTotal() + " ha sido procesada exitosamente."
        );

        return ventaGuardada;
    }
}