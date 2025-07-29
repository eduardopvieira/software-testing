package baseados_em_propriedade;

import model.entities.Duende;
import net.jqwik.api.*;
import net.jqwik.api.constraints.DoubleRange;
import net.jqwik.api.constraints.IntRange;
import net.jqwik.api.constraints.LongRange;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DuendeTest {

    @Property(tries = 100)
    @Label("[PBT] addCoins deve sempre somar corretamente ao saldo")
    void addCoinsProperty(@ForAll @IntRange(min = 1, max = 1000) int id, @ForAll @LongRange(min = 0, max = 5_000_000) long amountToAdd) {
        Duende duende = new Duende(id);
        long initialCoins = duende.getCoins();

        duende.addCoins(amountToAdd);

        assertThat(duende.getCoins()).isEqualTo(initialCoins + amountToAdd);
    }

    @Property(tries = 100)
    @Label("beingStealed deve sempre reduzir moedas pela metade")
    void beingStealedProperty(@ForAll @IntRange(min = 1, max = 1000) int id) {
        Duende duende = new Duende(id);
        long initialCoins = duende.getCoins();

        long stolenAmount = duende.beingStealed();

        assertThat(stolenAmount).isEqualTo(initialCoins / 2);
        assertThat(duende.getCoins()).isEqualTo(initialCoins - stolenAmount);
    }

    @Property(tries = 100)
    @Label("setPosition deve funcionar para qualquer valor não-negativo")
    void setPositionWithValidProperty(@ForAll @IntRange(min = 1, max = 1000) int id, @ForAll @DoubleRange(min = 0, max = 1_000_000) double validPosition) {
        Duende duende = new Duende(id);

        duende.setPosition(validPosition);

        assertThat(duende.getPosition()).isEqualTo(validPosition);
    }

    @Property(tries = 100)
    @Label("setPosition deve lançar exceção para qualquer valor negativo")
    void setPositionWithInvalidProperty(
            @ForAll @IntRange(min = 1, max = 1000) int id,
            @ForAll @DoubleRange(min = -1000000.0, max = -1.0) double invalidPosition
    ){
        Duende duende = new Duende(id);

        assertThatThrownBy(() -> {
            duende.setPosition(invalidPosition);
        }).isInstanceOf(IllegalArgumentException.class);
    }
}
