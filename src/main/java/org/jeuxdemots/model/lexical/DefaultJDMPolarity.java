package org.jeuxdemots.model.lexical;

import org.jeuxdemots.model.api.lexical.JDMPolarity;

public class DefaultJDMPolarity implements JDMPolarity {

    private final double negative;
    private final double neutral;
    private final double positive;

    public DefaultJDMPolarity(final double negative, final double neutral, final double positive) {
        this.negative = negative;
        this.neutral = neutral;
        this.positive = positive;
    }

    @Override
    public double getNegative() {
        return negative;
    }

    @Override
    public double getNeutral() {
        return neutral;
    }

    @Override
    public double getPositive() {
        return positive;
    }

    @Override
    public String toString() {
        return String.format("Polarity [Neg=%.2f%%, Neutr=%.2f%%, Pos=%.2f%%]", negative, neutral, positive);
    }
}
