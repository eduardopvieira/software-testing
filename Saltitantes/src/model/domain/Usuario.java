package model.domain;

public class Usuario {
    private int id;
    private String login;
    private String avatar;
    private int pontuacao;
    private int simulacoesExecutadas;

    public Usuario(int id, String login, String avatar, int pontuacao, int simulacoesExecutadas) {
        this.id = id;
        this.login = login;
        this.avatar = avatar;
        this.pontuacao = pontuacao;
        this.simulacoesExecutadas = simulacoesExecutadas;
    }

    public int getId() { return id; }
    public String getLogin() { return login; }
    public String getAvatar() { return avatar; }
    public int getPontuacao() { return pontuacao; }
    public int getSimulacoesExecutadas() { return simulacoesExecutadas; }

    @Override
    public String toString() {
        return "Usuario{" + "id=" + id + ", login='" + login + '\'' + ", pontuacao=" + pontuacao + ", simulacoesExecutadas=" + simulacoesExecutadas + '}';
    }
}
