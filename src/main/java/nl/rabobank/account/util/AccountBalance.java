package nl.rabobank.account.util;

@FunctionalInterface
public interface AccountBalance {

    Double apply(Double a, Double b);

}
