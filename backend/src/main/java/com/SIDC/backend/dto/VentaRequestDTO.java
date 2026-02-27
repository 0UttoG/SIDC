package com.SIDC.backend.dto;

import java.util.List;

public record VentaRequestDTO(
        Long idCliente,
        Long idVendedor,
        Long idRuta,
        Boolean esCredito,
        List<VentaDetalleRequestDTO> detalles
) {}