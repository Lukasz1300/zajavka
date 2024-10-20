package projektZajavka2.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import projektZajavka2.service.CurrencyService;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CurrencyController.class)
@ActiveProfiles("test")
public class CurrencyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CurrencyService currencyService;

    @Test
    @WithMockUser(username = "testuser", roles = {"ADMIN"})
    public void testGetUsdRate() throws Exception {
        // Ustawienie oczekiwanego wyniku
        String expectedRate = "4.25";
        when(currencyService.getUsdExchangeRate()).thenReturn(expectedRate);

        // Wykonanie testu
        mockMvc.perform(get("/api/usd-rate"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(content().json("{\"rate\":\"" + expectedRate + "\"}"));
    }
}
