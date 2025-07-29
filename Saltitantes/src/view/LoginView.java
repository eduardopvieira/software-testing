package view;

import model.dao.UsuarioDAO;

import javax.swing.*;
import java.awt.*;

public class LoginView {
    private JFrame frame;
    private JTextField loginField;
    private JPasswordField senhaField;
    private UsuarioDAO usuarioDAO;

    public LoginView() {
        this.usuarioDAO = new UsuarioDAO();
        criarJanela();
    }

    public void exibir() {
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void criarJanela() {
        frame = new JFrame("Login - Simulação de Duendes");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(450, 250);
        frame.setLayout(new BorderLayout(20, 20));

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel loginLabel = new JLabel("Login:");
        loginField = new JTextField(20);
        loginField.setName("loginField"); // Adicionar esta linha

        JLabel senhaLabel = new JLabel("Senha:");
        senhaField = new JPasswordField(20);
        senhaField.setName("senhaField"); // Adicionar esta linha

        loginLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        loginField.setAlignmentX(Component.LEFT_ALIGNMENT);
        senhaLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        senhaField.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton loginButton = new JButton("Fazer Login");
        loginButton.setName("loginButton"); // Adicionar esta linha

        JButton criarContaButton = new JButton("Criar Conta");
        criarContaButton.setName("criarContaButton"); // Adicionar esta linha

        JPanel botoesPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        botoesPanel.add(loginButton);
        botoesPanel.add(criarContaButton);
        botoesPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(loginLabel);
        panel.add(loginField);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(senhaLabel);
        panel.add(senhaField);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(botoesPanel);

        loginButton.addActionListener(e -> tentarLogin());
        criarContaButton.addActionListener(e -> abrirDialogoCriarConta());

        frame.add(panel, BorderLayout.CENTER);
    }

    private void tentarLogin() {
        String login = loginField.getText();
        String senha = new String(senhaField.getPassword());

        if (login.isEmpty() || senha.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Login e senha não podem estar vazios.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (usuarioDAO.verificarSenha(login, senha)) {
            frame.dispose();
            new ConfigSimulacaoView(login).exibir();
        } else {
            JOptionPane.showMessageDialog(frame, "Login ou senha inválidos.", "Falha no Login", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void abrirDialogoCriarConta() {
        JDialog dialogo = new JDialog(frame, "Criar Nova Conta", true);
        dialogo.setLayout(new BorderLayout());

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Login:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0;

        JTextField novoLoginField = new JTextField(15);
        novoLoginField.setName("novoLoginField"); // Adicionado
        panel.add(novoLoginField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Senha:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1;
        
        JPasswordField novaSenhaField = new JPasswordField(15);
        novaSenhaField.setName("novaSenhaField"); // Adicionado
        panel.add(novaSenhaField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        panel.add(new JSeparator(), gbc);

        gbc.gridy = 3;
        panel.add(new JLabel("Escolha seu avatar:"), gbc);

        String avatar1Path = "/model/avatar/avatar-duende.jpg";
        String avatar2Path = "/model/avatar/avatar-guardian.jpg";
        ImageIcon avatar1Icon = null;
        ImageIcon avatar2Icon = null;
        try {
            Image img1 = new ImageIcon(getClass().getResource(avatar1Path)).getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
            Image img2 = new ImageIcon(getClass().getResource(avatar2Path)).getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
            avatar1Icon = new ImageIcon(img1);
            avatar2Icon = new ImageIcon(img2);
        } catch (Exception e) {
            System.err.println("Erro ao carregar imagens de avatar: " + e.getMessage());
        }

        JRadioButton rbAvatar1 = new JRadioButton("Duende", avatar1Icon);
        rbAvatar1.setName("avatarDuendeRadio");
        rbAvatar1.setActionCommand(avatar1Path);

        JRadioButton rbAvatar2 = new JRadioButton("Guardião", avatar2Icon);
        rbAvatar2.setName("avatarGuardiaoRadio");
        rbAvatar2.setActionCommand(avatar2Path);

        ButtonGroup avatarGroup = new ButtonGroup();
        avatarGroup.add(rbAvatar1);
        avatarGroup.add(rbAvatar2);

        JPanel avatarPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        avatarPanel.add(rbAvatar1);
        avatarPanel.add(rbAvatar2);
        gbc.gridy = 4;
        panel.add(avatarPanel, gbc);

        gbc.gridy = 5; gbc.gridwidth = 2;
        panel.add(new JSeparator(), gbc);

        JButton confirmarButton = new JButton("Confirmar");
        confirmarButton.setName("confirmarButton"); // Adicionar esta linha
        JButton cancelarButton = new JButton("Cancelar");
        cancelarButton.setName("cancelarButton"); // Adicionar esta linha

        JPanel botoesPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botoesPanel.add(cancelarButton);
        botoesPanel.add(confirmarButton);

        dialogo.add(panel, BorderLayout.CENTER);
        dialogo.add(botoesPanel, BorderLayout.SOUTH);

        confirmarButton.addActionListener(e -> {
            String login = novoLoginField.getText();
            String senha = new String(novaSenhaField.getPassword());

            if (login.trim().isEmpty() || senha.trim().isEmpty()) {
                JOptionPane.showMessageDialog(dialogo, "Login e senha não podem ser vazios.", "Erro de Validação", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (avatarGroup.getSelection() == null) {
                JOptionPane.showMessageDialog(dialogo, "Por favor, selecione um avatar.", "Erro de Validação", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String avatarSelecionado = avatarGroup.getSelection().getActionCommand();

            if (usuarioDAO.adicionarUsuario(login, senha, avatarSelecionado)) {
                JOptionPane.showMessageDialog(frame, "Conta criada com sucesso! Agora você pode fazer o login.", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                dialogo.dispose();
            } else {
                JOptionPane.showMessageDialog(dialogo, "Não foi possível criar a conta. O login pode já estar em uso.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelarButton.addActionListener(e -> dialogo.dispose());

        dialogo.pack();
        dialogo.setLocationRelativeTo(frame);
        dialogo.setVisible(true);
    }

    public JFrame getFrame() {
        return frame;
    }
}
