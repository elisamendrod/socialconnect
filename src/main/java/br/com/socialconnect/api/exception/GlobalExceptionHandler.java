package br.com.socialconnect.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // ✅ Erro de validação (400 Bad Request)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleValidation(
            MethodArgumentNotValidException ex, WebRequest request) {

        boolean estoqueNegativo = ex.getBindingResult().getFieldErrors().stream().anyMatch(e -> "EstoqueNaoNegativo".equals(e.getCode()));
        List<ProblemDetail.FieldError> errors = ex.getBindingResult()
                .getFieldErrors().stream()
                .map(e -> new ProblemDetail.FieldError(
                        e.getField(),
                        e.getDefaultMessage()
                ))
                .collect(Collectors.toList());

        ProblemDetail problem = new ProblemDetail(
                "https://socialconnect.api/errors/validacao",
                "Erro de validação",
                estoqueNegativo ? 422 : 400,
                "Um ou mais campos são inválidos.",
                request.getDescription(false).replace("uri=", ""),
                LocalDateTime.now(),
                errors
        );

        return ResponseEntity.status(estoqueNegativo ? 422 : 400).contentType(org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON).body(problem);
    }

    // ✅ CPF duplicado (409 Conflict)
    @ExceptionHandler(CpfDuplicadoException.class)
    public ResponseEntity<ProblemDetail> handleCpfDuplicado(
            CpfDuplicadoException ex, WebRequest request) {

        ProblemDetail problem = new ProblemDetail(
                "https://socialconnect.api/errors/cpf-duplicado",
                "CPF já cadastrado",
                HttpStatus.CONFLICT.value(),
                ex.getMessage(),
                request.getDescription(false).replace("uri=", ""),
                LocalDateTime.now(),
                List.of()
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(problem);
    }

    // ✅ Recurso não encontrado (404 Not Found)
    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<ProblemDetail> handleNaoEncontrado(
            RecursoNaoEncontradoException ex, WebRequest request) {

        ProblemDetail problem = new ProblemDetail(
                "https://socialconnect.api/errors/nao-encontrado",
                "Recurso não encontrado",
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                request.getDescription(false).replace("uri=", ""),
                LocalDateTime.now(),
                List.of()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).contentType(org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON).body(problem);
    }

    // ✅ Erro genérico (500 Internal Server Error)
    @ExceptionHandler({EstoqueNegativoException.class, ProdutoDuplicadoException.class})
    public ResponseEntity<ProblemDetail> handleProduto(RuntimeException ex, WebRequest request) {
        int status = ex instanceof EstoqueNegativoException ? 422 : 409;
        ProblemDetail problem = new ProblemDetail("about:blank", status == 422 ? "Estoque invalido" : "Nome duplicado",
            status, ex.getMessage(), request.getDescription(false).replace("uri=", ""), LocalDateTime.now(), List.of());
        return ResponseEntity.status(status).contentType(org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON).body(problem);
    }

    @ExceptionHandler({org.springframework.http.converter.HttpMessageNotReadableException.class,
        org.springframework.web.method.annotation.MethodArgumentTypeMismatchException.class})
    public ResponseEntity<ProblemDetail> handleEntrada(Exception ex, WebRequest request) {
        ProblemDetail problem = new ProblemDetail("about:blank", "Requisicao invalida", 400,
            "Confira o JSON e os parametros informados.", request.getDescription(false).replace("uri=", ""), LocalDateTime.now(), List.of());
        return ResponseEntity.badRequest().contentType(org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON).body(problem);
    }
    @ExceptionHandler(OrdenacaoInvalidaException.class)
    public ResponseEntity<ProblemDetail> handleOrdenacao(OrdenacaoInvalidaException ex, WebRequest request) {
        ProblemDetail problem = new ProblemDetail("about:blank", "Ordenacao invalida", 400,
                ex.getMessage(), request.getDescription(false).replace("uri=", ""), LocalDateTime.now(), List.of());
        return ResponseEntity.badRequest().contentType(org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON).body(problem);
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleGenerico(
            Exception ex, WebRequest request) {

        ProblemDetail problem = new ProblemDetail(
                "https://socialconnect.api/errors/erro-interno",
                "Erro interno do servidor",
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Ocorreu um erro inesperado. Tente novamente.",
                request.getDescription(false).replace("uri=", ""),
                LocalDateTime.now(),
                List.of()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problem);
    }
}
