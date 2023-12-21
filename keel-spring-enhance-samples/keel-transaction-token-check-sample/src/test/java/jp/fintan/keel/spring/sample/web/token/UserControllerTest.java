package jp.fintan.keel.spring.sample.web.token;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.xpath;

import jp.fintan.keel.spring.web.token.transaction.TransactionToken;
import jp.fintan.keel.spring.web.token.transaction.TransactionTokenInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import org.junit.jupiter.api.Test;

@SpringBootTest(classes = TransactionTokenCheckSampleApplication.class)
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testToConfirmIfTokensAreBeingGenerated() throws Exception {
        mockMvc
                .perform(post("/confirm")
                        .param("name", "taro")
                )
                .andExpect(status().isOk())
                .andExpect(model().hasNoErrors())
                .andExpect(view().name("confirm"))
                .andExpect(xpath("//*[@name=\"_TRANSACTION_TOKEN\"]/@value").exists());

    }

    @Test
    void testToConfirmErrorOccursIfNotSendingToken() throws Exception {
        mockMvc
                .perform(post("/create")
                        .param("name", "taro")
                )
                .andExpect(status().isBadRequest())
                .andExpect(view().name("error/token-error"));
    }

    @Test
    void testToConfirmThatItNormallyIfSendingToken() throws Exception {

        final MockHttpServletRequest request = mockMvc
                .perform(post("/confirm")
                        .param("name", "taro")
                )
                .andExpect(status().isOk())
                .andReturn()
                .getRequest();

        MockHttpSession session = (MockHttpSession) request.getSession();
        TransactionToken transactionToken = (TransactionToken) request.getAttribute(TransactionTokenInterceptor.NEXT_TOKEN_REQUEST_ATTRIBUTE_NAME);

        mockMvc
                .perform(
                        post("/create")
                                .param("name", "taro")
                                .param("_TRANSACTION_TOKEN", transactionToken.getTokenString())
                                .session(session))
                .andExpect(redirectedUrl("/complete"));
    }

}
