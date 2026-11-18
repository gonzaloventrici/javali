package com.javali.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final EvaluacionInicialInterceptor
            evaluacionInicialInterceptor;

    public WebMvcConfig(
            EvaluacionInicialInterceptor
                    evaluacionInicialInterceptor) {
        this.evaluacionInicialInterceptor =
                evaluacionInicialInterceptor;
    }

    @Override
    public void addInterceptors(
            InterceptorRegistry registry) {
        registry.addInterceptor(
                        evaluacionInicialInterceptor
                )
                .addPathPatterns(
                        "/web/cursos/**",
                        "/web/unidades/**",
                        "/web/lecciones/**",
                        "/web/ejercicios/**",
                        "/web/mi-progreso"
                )
                .excludePathPatterns(
                        "/web/evaluacion",
                        "/web/evaluaciones/**",
                        "/login",
                        "/logout",
                        "/registro",
                        "/error"
                );
    }
}