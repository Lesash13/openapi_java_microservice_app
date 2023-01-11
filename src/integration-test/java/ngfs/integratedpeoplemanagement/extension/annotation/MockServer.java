package ngfs.integratedpeoplemanagement.extension.annotation;

import ngfs.integratedpeoplemanagement.extension.MockServerExtension;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@RestTestClient
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockServerExtension.class)
public @interface MockServer {
}
