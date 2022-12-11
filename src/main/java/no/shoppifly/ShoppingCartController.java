package no.shoppifly;

import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController()
public class ShoppingCartController implements ApplicationListener<ApplicationReadyEvent> {

//    @Autowired
//    private Map<String, Cart> theCart = new HashMap<>();

    @Autowired
    private NaiveCartImpl naiveCart;

    @Autowired
    private MeterRegistry meterRegistry;
    @Autowired
    private final CartService cartService;

    public ShoppingCartController(MeterRegistry meterRegistry, CartService cartService) {
        this.meterRegistry = meterRegistry;
        this.cartService = cartService;
    }
    @Timed
    @GetMapping(path = "/cart/{id}")
    public Cart getCart(@PathVariable String id) {
        meterRegistry.counter("carts").increment();
        return cartService.getCart(id);
    }

    /**
     * Checks out a shopping cart. Removes the cart, and returns an order ID
     *
     * @return an order ID
     */
    @PostMapping(path = "/cart/checkout")
    public String checkout(@RequestBody Cart cart) {
        return cartService.checkout(cart);
    }

    /**
     * Updates a shopping cart, replacing it's contents if it already exists. If no cart exists (id is null)
     * a new cart is created.
     *
     * @return the updated cart
     */
    @PostMapping(path = "/cart")
    public Cart updateCart(@RequestBody Cart cart) {
        return cartService.update(cart);
    }

    /**
     * return all cart IDs
     *
     * @return
     */
    @GetMapping(path = "/carts")
    public List<String> getAllCarts() {
        return cartService.getAllsCarts();
    }


    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
        Gauge.builder("cart_count", naiveCart.shoppingCarts,
                b -> b.values().size()).register(meterRegistry);
    }


}