package ngfs.integratedpeoplemanagement.extension.annotation;

import ngfs.integratedpeoplemanagement.extension.RestAssuredClientExtension;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(RestAssuredClientExtension.class)
public @interface RestTestClient {
}
