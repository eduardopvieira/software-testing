package model.domain;

public class Usuario {
    private int id;
    private String login;
    private String avatar;
    private long pontuacao;

    public Usuario(int id, String login, String avatar, int pontuacao) {
        this.id = id;
        this.login = login;
        this.avatar = avatar;
        this.pontuacao = pontuacao;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }
    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }
    public long getPontuacao() { return pontuacao; }
    public void setPontuacao(int pontuacao) { this.pontuacao = pontuacao; }

    @Override
    public String toString() {
        return "Usuario{" +
                "id=" + id +
                ", login='" + login + '\'' +
                ", avatar='" + avatar + '\'' +
                ", pontuacao=" + pontuacao +
                '}';
    }
}