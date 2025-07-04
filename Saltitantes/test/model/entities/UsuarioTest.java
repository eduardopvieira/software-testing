package model.entities;

import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.constraints.IntRange;
import net.jqwik.api.constraints.StringLength;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UsuarioTest {

    @Test
    @DisplayName("Teste de Domínio: Construtor e getters devem funcionar com dados de exemplo")
    void constructorEGetters_devemAtribuirERetornarValoresCorretamente() {
        int id = 1;
        String login = "jogador1";
        String avatar = "/avatares/avatar1.png";
        int pontuacao = 10;
        int simulacoes = 25;

        Usuario usuario = new Usuario(id, login, avatar, pontuacao, simulacoes);

        assertThat(usuario.getId()).isEqualTo(id);
        assertThat(usuario.getLogin()).isEqualTo(login);
        assertThat(usuario.getAvatar()).isEqualTo(avatar);
        assertThat(usuario.getPontuacao()).isEqualTo(pontuacao);
        assertThat(usuario.getSimulacoesExecutadas()).isEqualTo(simulacoes);
    }

    @Test
    @DisplayName("Teste Estrutural: Método toString deve retornar a string formatada corretamente")
    void toString_deveGerarStringFormatada() {
        // Arrange
        Usuario usuario = new Usuario(1, "jogador1", "/avatares/avatar1.png", 10, 25);
        String expectedString = "Usuario{id=1, login='jogador1', pontuacao=10, simulacoesExecutadas=25}";

        String actualString = usuario.toString();

        assertThat(actualString).isEqualTo(expectedString);
    }

    @Property
    void constructorProperty(
            @ForAll @IntRange(min = 1) int id,
            @ForAll @StringLength(min = 1, max = 50) String login,
            @ForAll @StringLength(min = 1, max = 100) String avatar,
            @ForAll @IntRange(min = 0) int pontuacao,
            @ForAll @IntRange(min = 0) int simulacoes
    ) {

        Usuario usuario = new Usuario(id, login, avatar, pontuacao, simulacoes);

        assertThat(usuario.getId()).isEqualTo(id);
        assertThat(usuario.getLogin()).isEqualTo(login);
        assertThat(usuario.getAvatar()).isEqualTo(avatar);
        assertThat(usuario.getPontuacao()).isEqualTo(pontuacao);
        assertThat(usuario.getSimulacoesExecutadas()).isEqualTo(simulacoes);
    }
}
