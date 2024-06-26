package ru.shotin.spring.demo.project1.currency.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.shotin.spring.demo.project1.config.TarantoolConfigEnum;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/currency/v1")
@Slf4j
public class CurrencyController {

    record ConversionKey(String from, String to) {
    }

    private final Map<ConversionKey, Double> conversionRates = new ConcurrentHashMap<>();

    {
        conversionRates.put(new ConversionKey("RUB", "USD"), 95.0);
        conversionRates.put(new ConversionKey("RUB", "EUR"), 105.0);
    }

    @GetMapping("/conversion")
    public ResponseEntity<Map<String, Object>> convert(
            @RequestParam Double amount, @RequestParam String from, @RequestParam String to
    ) {
        log.info(TarantoolConfigEnum.TARANTOOL_ONE.getHost());
        log.info(TarantoolConfigEnum.TARANTOOL_TWO.getHost());
        var rate = conversionRates.get(new ConversionKey(from, to));
        if (rate == null) {
            return ResponseEntity.notFound().build();
        }
        Map<String, Object> conversionResult = new LinkedHashMap<>();
        conversionResult.put("from", Map.of(
                "currency", from,
                "amount", amount));
        conversionResult.put("to", Map.of(
                "currency", to,
                "amount", amount * rate));
        return ResponseEntity.ok(conversionResult);
    }
}
