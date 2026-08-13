package com.obysoft.escalafacil.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public final class RegraPlantao24h {

    public static final LocalTime HORA_INICIO = LocalTime.of(8, 0);

    private RegraPlantao24h() {
    }

    public static LocalDateTime inicio(LocalDate data) {
        return data.atTime(HORA_INICIO);
    }

    public static LocalDateTime fim(LocalDate data) {
        return inicio(data).plusHours(24);
    }

    public static boolean podeEscalar(LocalDate ultimoPlantao, LocalDate novoPlantao) {
        return ultimoPlantao == null
                || ultimoPlantao.plusDays(1).isBefore(novoPlantao);
    }
}