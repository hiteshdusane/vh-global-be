package in.vhglobal.ecommerce.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = {ErrorResponseTest.TestApplication.class, JsonErrorController.class},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ErrorResponseTest {
    @Autowired
    private TestRestTemplate client;

    @Autowired
    private ObjectMapper objectMapper;

    @ParameterizedTest
    @ValueSource(ints = {404, 405, 500})
    void errorDispatchReplacesInheritedPlainTextContentType(int status) throws Exception {
        var response = client.getForEntity("/test/plain-error?status=" + status, String.class);

        assertThat(response.getStatusCode().value()).isEqualTo(status);
        assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
        var body = objectMapper.readTree(response.getBody());
        assertThat(body.get("status").asInt()).isEqualTo(status);
        assertThat(body.get("path").asText()).isEqualTo("/test/plain-error");
    }

    @ParameterizedTest
    @ValueSource(strings = {"image/jpeg", "application/octet-stream"})
    void errorDispatchReplacesOtherResourceContentTypes(String contentType) throws Exception {
        var response = client.getForEntity("/test/plain-error?status=404&contentType=" + contentType, String.class);

        assertThat(response.getStatusCode().value()).isEqualTo(404);
        assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
        assertThat(objectMapper.readTree(response.getBody()).get("status").asInt()).isEqualTo(404);
    }

    @Test
    void successfulPlainTextResponseKeepsItsContentType() {
        var response = client.getForEntity("/test/plain-error?status=200", String.class);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getHeaders().getContentType().isCompatibleWith(MediaType.TEXT_PLAIN)).isTrue();
        assertThat(response.getBody()).isEqualTo("OK");
    }

    @TestConfiguration(proxyBeanMethods = false)
    @EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class, SecurityAutoConfiguration.class})
    @Import(ControllerAdvice.class)
    static class TestApplication {
        @Bean
        ServletRegistrationBean<HttpServlet> plainTextErrorServlet() {
            return new ServletRegistrationBean<>(new HttpServlet() {
                @Override
                protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
                    String contentType = request.getParameter("contentType");
                    response.setContentType(contentType == null ? MediaType.TEXT_PLAIN_VALUE : contentType);
                    int status = Integer.parseInt(request.getParameter("status"));
                    if (status == 200) {
                        response.getWriter().write("OK");
                    } else {
                        response.sendError(status);
                    }
                }
            }, "/test/plain-error");
        }
    }
}
