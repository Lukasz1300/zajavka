package projektZajavka2.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import projektZajavka2.entity.RestaurantOwner;
import projektZajavka2.repository.RestaurantOwnerRepository;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
@AutoConfigureMockMvc
@WithMockUser(username = "testuser", roles = {"ADMIN"})
@ActiveProfiles("test")
public class RestaurantOwnerControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private Long savedOwnerId;
    @Autowired
    private RestaurantOwnerRepository restaurantOwnerRepository;

    @BeforeEach
    public void setUp() {

        RestaurantOwner owner = new RestaurantOwner();
        owner.setName("Test Owner");
        owner.setEmail("owner@test.com");
        owner.setPhoneNumber("+1234567890");
        owner.setCreatedAt(LocalDateTime.now());
        restaurantOwnerRepository.save(owner);
        this.savedOwnerId = owner.getId();  // Zapisanie ID dla późniejszego użycia
    }

    @Test
    public void testGetOwnerById() throws Exception {
        //   RestaurantOwner owner = restaurantOwnerRepository.findAll().get(0);
        mockMvc.perform(get("/restaurant-owners/{id}", savedOwnerId))
                .andExpect(status().isOk())
                .andExpect(view().name("restaurantowners/details"))
                .andExpect(model().attributeExists("owner"))
                .andExpect(model().attribute("owner", hasProperty("name", is("Test Owner"))));
    }
}
