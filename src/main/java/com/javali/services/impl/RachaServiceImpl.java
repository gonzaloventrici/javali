package com.javali.services.impl;

import com.javali.dtos.RachaResponse;
import com.javali.repositories.IntentoRepository;
import com.javali.services.RachaService;
import com.javali.utils.RachaUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class RachaServiceImpl implements RachaService {

    private final IntentoRepository intentoRepository;

    public RachaServiceImpl(IntentoRepository intentoRepository) {
        this.intentoRepository = intentoRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public RachaResponse obtener(Long usuarioId) {
        return obtener(usuarioId, LocalDate.now());
    }

    @Override
    @Transactional(readOnly = true)
    public RachaResponse obtener(Long usuarioId, LocalDate hoy) {
        List<LocalDate> dias =
                intentoRepository.listarDiasConIntentos(usuarioId);
        return new RachaResponse(
                RachaUtils.rachaActual(dias, hoy),
                RachaUtils.mejorRacha(dias),
                dias.contains(hoy)
        );
    }
}
