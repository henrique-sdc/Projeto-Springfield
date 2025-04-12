package com.springfield.springfield_api;

import com.springfield.springfield_api.repository.UsuarioRepository;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;

@Configuration
public class MetricsConfiguration {

    private final MeterRegistry meterRegistry;
    private final UsuarioRepository usuarioRepository;

    public MetricsConfiguration(MeterRegistry meterRegistry, UsuarioRepository usuarioRepository) {
        this.meterRegistry = meterRegistry;
        this.usuarioRepository = usuarioRepository;
    }

    @PostConstruct
    public void registerUserCountGauge() {
        String metricName = "springfield_registered_users_count";

        Iterable<Tag> tags = Collections.emptyList();

        Gauge.builder(metricName, usuarioRepository, repo -> repo.count())
                .description("Número total de usuários registrados na plataforma Springfield")
                .baseUnit("users")
                .tags(tags)
                .register(meterRegistry);
    }
}