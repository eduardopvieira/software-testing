package baseados_em_propriedade;

import model.entities.Cluster;
import model.entities.Duende;
import net.jqwik.api.*;
import net.jqwik.api.constraints.IntRange;

import static org.assertj.core.api.Assertions.assertThat;

class ClusterTest {

    private Duende createDuende(int id) {
        return new Duende(id);
    }

    @Property
    @Label("construtor deve somar corretamente os valores dos duendes iniciais")
    void constructorProperty(@ForAll @IntRange(min = 1) int id1, @ForAll @IntRange(min = 1) int id2) {
        Duende duende1 = createDuende(id1);
        Duende duende2 = createDuende(id2);
        long expectedCoins = duende1.getCoins() + duende2.getCoins();

        Cluster cluster = new Cluster(duende1, duende2);

        assertThat(cluster.getQuantityDuendes()).isEqualTo(2);
        assertThat(cluster.getCoins()).isEqualTo(expectedCoins);
    }

    @Property
    @Label("adicionar um duende deve atualizar corretamente o estado do cluster")
    void addDuendeToClusterProperty(@ForAll @IntRange(min = 1) int id1, @ForAll @IntRange(min = 1) int id2, @ForAll @IntRange(min = 1) int id3) {
        Duende d1 = createDuende(id1);
        Duende d2 = createDuende(id2);
        Duende d3 = createDuende(id3);
        Cluster cluster = new Cluster(d1, d2);
        long initialCoins = cluster.getCoins();
        int initialQty = cluster.getQuantityDuendes();

        cluster.addToCluster(d3);

        assertThat(cluster.getCoins()).isEqualTo(initialCoins + d3.getCoins());
        assertThat(cluster.getQuantityDuendes()).isEqualTo(initialQty + 1);
    }

    @Property
    @Label("beingStealed deve sempre reduzir moedas pela metade")
    void beingStealedProperty(@ForAll @IntRange(min = 1) int id1, @ForAll @IntRange(min = 1) int id2) {
        Cluster cluster = new Cluster(createDuende(id1), createDuende(id2));
        long initialCoins = cluster.getCoins();

        long stolenAmount = cluster.beingStealed();

        assertThat(stolenAmount).isEqualTo(initialCoins / 2);
        assertThat(cluster.getCoins()).isEqualTo(initialCoins - stolenAmount);
    }
}
