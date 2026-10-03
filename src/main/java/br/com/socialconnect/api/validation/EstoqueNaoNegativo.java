package br.com.socialconnect.api.validation;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.RECORD_COMPONENT})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = EstoqueNaoNegativoValidator.class)
public @interface EstoqueNaoNegativo {
    String message() default "{produto.estoque.negativo}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
