package nl.rabobank.account.controller;

import lombok.RequiredArgsConstructor;
import nl.rabobank.account.model.CurrentBalanceResponse;
import nl.rabobank.account.model.TransferRequest;
import nl.rabobank.account.model.WithdrawalRequest;
import nl.rabobank.account.service.AccountService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/account")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @GetMapping("/current-balance")
    public Flux<CurrentBalanceResponse> getBalance() {
        return accountService.getBalance();
    }

    @PostMapping("/withdraw")
    public Mono<Void> withdraw(@RequestBody WithdrawalRequest withdrawalRequest) {
        return accountService.withdraw(withdrawalRequest);
    }

    @PostMapping("/transfer")
    public Mono<Void> transfer(@RequestBody TransferRequest transferRequest) {
        return accountService.transfer(transferRequest);
    }

    @PostMapping("/pay")
    public Mono<Void> payment(@RequestBody TransferRequest transferRequest) {
        return null; //accountService.payment(transferRequest);
    }

}
