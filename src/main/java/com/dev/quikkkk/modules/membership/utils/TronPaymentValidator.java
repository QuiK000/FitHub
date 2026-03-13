package com.dev.quikkkk.modules.membership.utils;

import com.dev.quikkkk.core.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static com.dev.quikkkk.core.enums.ErrorCode.INVALID_PAYMENT_AMOUNT;
import static com.dev.quikkkk.core.enums.ErrorCode.PAYMENT_VALIDATION_ERROR;
import static com.dev.quikkkk.core.enums.ErrorCode.TRANSACTION_FAILED;
import static com.dev.quikkkk.core.enums.ErrorCode.TRANSACTION_NOT_FOUND;
import static com.dev.quikkkk.core.enums.ErrorCode.UNSUPPORTED_TRANSACTION_TYPE;

@Service
@RequiredArgsConstructor
@Slf4j
public class TronPaymentValidator {
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${tron.api.url:https://nile.trongrid.io}")
    private String tronApiUrl;

    public void validateTransaction(String txHash, BigDecimal expectedAmount) {
        log.info("Validating Tron transaction: {}", txHash);
        String url = tronApiUrl + "/wallet/gettransactionbyid?value=" + txHash;

        try {
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            JsonNode root = objectMapper.readTree(response.getBody());

            if (root.isEmpty() || !root.has("ret")) {
                log.error("Transaction not found in TronGrid: {}", txHash);
                throw new BusinessException(TRANSACTION_NOT_FOUND);
            }

            String status = root.get("ret").get(0).get("contractRet").asText();
            if (!"SUCCESS".equals(status)) {
                log.error("Transaction failed on blockchain. Status: {}", status);
                throw new BusinessException(TRANSACTION_FAILED);
            }

            JsonNode rawData = root.get("raw_data");
            JsonNode contract = rawData.get("contract").get(0);
            JsonNode parameter = contract.get("parameter").get("value");

            String contractType = contract.get("type").asText();
            if (!"TransferContract".equals(contractType)) throw new BusinessException(UNSUPPORTED_TRANSACTION_TYPE);

            long amountInSun = parameter.get("amount").asLong();
            BigDecimal amountInTrx = BigDecimal.valueOf(amountInSun)
                    .divide(BigDecimal.valueOf(1_000_000), 6, RoundingMode.HALF_UP);

            if (amountInTrx.compareTo(expectedAmount) < 0) {
                log.error("Insufficient payment amount. Expected: {}, Got: {}", expectedAmount, amountInTrx);
                throw new BusinessException(INVALID_PAYMENT_AMOUNT);
            }

            log.info("Transaction {} validated successfully. Amount: {} TRX", txHash, amountInTrx);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error connecting to TronGrid", e);
            throw new BusinessException(PAYMENT_VALIDATION_ERROR);
        }
    }
}
