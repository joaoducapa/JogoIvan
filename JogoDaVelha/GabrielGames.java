import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.swing.*;

public class GabrielGames extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    
    public GabrielGames() {
        setTitle("🎮 GABRIEL GAMES");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 600);
        setLocationRelativeTo(null);
        setResizable(false);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        mainPanel.add(new TelaInicio(this), "INICIO");
        mainPanel.add(new JogoDaVelhaPanel(this), "VELHA");
        mainPanel.add(new JogoMemoriaPanel(this), "MEMORIA");

        add(mainPanel);
        mostrarTela("INICIO");
    }

    public void mostrarTela(String nomeTela) {
        cardLayout.show(mainPanel, nomeTela);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new GabrielGames().setVisible(true);
        });
    }
}

// TELA INICIAL SIMPLES
class TelaInicio extends JPanel {
    private GabrielGames parent;

    public TelaInicio(GabrielGames parent) {
        this.parent = parent;
        setLayout(new BorderLayout());
        setBackground(new Color(25, 25, 60));

        JLabel titulo = new JLabel("🎮 GABRIEL GAMES", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 42));
        titulo.setForeground(Color.YELLOW);
        titulo.setBorder(BorderFactory.createEmptyBorder(50, 0, 40, 0));

        JPanel botoesPanel = new JPanel(new GridLayout(3, 1, 15, 15));
        botoesPanel.setBackground(new Color(25, 25, 60));
        botoesPanel.setBorder(BorderFactory.createEmptyBorder(0, 100, 80, 100));

        JButton btnVelha = criarBotao("⭕ JOGO DA VELHA", new Color(0, 150, 255));
        JButton btnMemoria = criarBotao("🎴 JOGO DA MEMÓRIA", new Color(180, 70, 200));
        JButton btnSair = criarBotao("🚪 SAIR", new Color(255, 80, 80));

        btnVelha.addActionListener(e -> parent.mostrarTela("VELHA"));
        btnMemoria.addActionListener(e -> parent.mostrarTela("MEMORIA"));
        btnSair.addActionListener(e -> System.exit(0));

        botoesPanel.add(btnVelha);
        botoesPanel.add(btnMemoria);
        botoesPanel.add(btnSair);

        add(titulo, BorderLayout.NORTH);
        add(botoesPanel, BorderLayout.CENTER);
    }

    private JButton criarBotao(String texto, Color cor) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Arial", Font.BOLD, 18));
        btn.setBackground(cor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }
}

// JOGO DA VELHA CORRIGIDO - AGORA VOCÊ JOGA!
class JogoDaVelhaPanel extends JPanel {
    private GabrielGames parent;
    private JButton[][] botoes;
    private boolean vezJogador; // true = jogador (X), false = IA (O)
    private JLabel statusLabel, placarLabel;
    private int jogadas, vitoriasJogador, vitoriasIA, empates;
    private boolean modoIA;
    private String dificuldade;
    private Random random;

    public JogoDaVelhaPanel(GabrielGames parent) {
        this.parent = parent;
        setLayout(new BorderLayout());
        setBackground(new Color(240, 240, 240));
        inicializarVariaveis();
        botoes = new JButton[3][3];

        add(criarCabecalho("⭕ JOGO DA VELHA"), BorderLayout.NORTH);
        add(criarTabuleiro(), BorderLayout.CENTER);
        add(criarPainelInferior(), BorderLayout.SOUTH);
    }

    private void inicializarVariaveis() {
        vezJogador = true; // Jogador começa
        jogadas = 0;
        vitoriasJogador = vitoriasIA = empates = 0;
        modoIA = true;
        dificuldade = "NORMAL";
        random = new Random();
    }

    private JPanel criarCabecalho(String titulo) {
        JPanel cabecalho = new JPanel(new BorderLayout());
        cabecalho.setBackground(new Color(70, 130, 180));
        cabecalho.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel lblTitulo = new JLabel(titulo, SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitulo.setForeground(Color.WHITE);

        JButton btnVoltar = new JButton("🏠 Menu");
        btnVoltar.setFont(new Font("Arial", Font.BOLD, 12));
        btnVoltar.addActionListener(e -> parent.mostrarTela("INICIO"));

        cabecalho.add(btnVoltar, BorderLayout.WEST);
        cabecalho.add(lblTitulo, BorderLayout.CENTER);
        return cabecalho;
    }

    private JPanel criarTabuleiro() {
        JPanel tabuleiroPanel = new JPanel(new GridLayout(3, 3, 8, 8));
        tabuleiroPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        tabuleiroPanel.setBackground(new Color(60, 60, 80));

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                botoes[i][j] = new JButton("");
                botoes[i][j].setFont(new Font("Arial", Font.BOLD, 50));
                botoes[i][j].setFocusPainted(false);
                botoes[i][j].setBackground(Color.WHITE);
                botoes[i][j].addActionListener(new BotaoClickListener(i, j));
                tabuleiroPanel.add(botoes[i][j]);
            }
        }
        return tabuleiroPanel;
    }

    private JPanel criarPainelInferior() {
        JPanel painel = new JPanel(new BorderLayout());
        painel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        statusLabel = new JLabel("🎯 SUA VEZ (X)", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 20));
        statusLabel.setForeground(Color.BLUE);

        placarLabel = new JLabel("📊 Você:0 | IA:0 | Empates:0", SwingConstants.CENTER);
        placarLabel.setFont(new Font("Arial", Font.BOLD, 14));

        JPanel controlesPanel = new JPanel(new GridLayout(1, 4, 5, 0));
        
        JButton btnReiniciar = criarBotaoControle("🔄 Reiniciar", Color.CYAN);
        JButton btnToggleIA = criarBotaoControle(modoIA ? "🤖 IA LIGADA" : "👥 2 JOGADORES", Color.ORANGE);
        JButton btnDificuldade = criarBotaoControle("💪 " + dificuldade, Color.MAGENTA);
        JButton btnVoltar = criarBotaoControle("🏠 Menu", Color.GRAY);

        btnReiniciar.addActionListener(e -> reiniciarJogo());
        btnToggleIA.addActionListener(e -> {
            modoIA = !modoIA;
            btnToggleIA.setText(modoIA ? "🤖 IA LIGADA" : "👥 2 JOGADORES");
            reiniciarJogo();
        });
        btnDificuldade.addActionListener(e -> {
            String[] dificuldades = {"FÁCIL", "NORMAL", "DIFÍCIL"};
            int index = java.util.Arrays.asList(dificuldades).indexOf(dificuldade);
            dificuldade = dificuldades[(index + 1) % dificuldades.length];
            btnDificuldade.setText("💪 " + dificuldade);
            reiniciarJogo();
        });
        btnVoltar.addActionListener(e -> parent.mostrarTela("INICIO"));

        controlesPanel.add(btnReiniciar);
        controlesPanel.add(btnToggleIA);
        controlesPanel.add(btnDificuldade);
        controlesPanel.add(btnVoltar);

        painel.add(statusLabel, BorderLayout.NORTH);
        painel.add(placarLabel, BorderLayout.CENTER);
        painel.add(controlesPanel, BorderLayout.SOUTH);
        return painel;
    }

    private JButton criarBotaoControle(String texto, Color cor) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Arial", Font.BOLD, 12));
        btn.setBackground(cor);
        btn.setForeground(Color.BLACK);
        btn.setFocusPainted(false);
        return btn;
    }

    private class BotaoClickListener implements ActionListener {
        private int linha, coluna;
        public BotaoClickListener(int linha, int coluna) {
            this.linha = linha;
            this.coluna = coluna;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            // Só permite jogar se for a vez do jogador E o botão estiver vazio
            if (!vezJogador || !botoes[linha][coluna].getText().equals("")) {
                return;
            }

            // Jogada do jogador (X)
            fazerJogada(linha, coluna, "X");
            
            // Se modo IA está ligado e o jogo não acabou, IA joga
            if (modoIA && !verificarFimDeJogo() && !vezJogador) {
                new Timer(800, evt -> jogadaIA()).start();
            }
        }
    }

    private void fazerJogada(int linha, int coluna, String jogador) {
        JButton botao = botoes[linha][coluna];
        
        if (jogador.equals("X")) {
            botao.setText("X");
            botao.setForeground(Color.BLUE);
            statusLabel.setText("🤖 VEZ DA IA (O)");
            statusLabel.setForeground(Color.RED);
            vezJogador = false; // Próxima vez é da IA
        } else {
            botao.setText("O");
            botao.setForeground(Color.RED);
            statusLabel.setText("🎯 SUA VEZ (X)");
            statusLabel.setForeground(Color.BLUE);
            vezJogador = true; // Próxima vez é do jogador
        }

        jogadas++;
        verificarVencedor();
    }

    private void jogadaIA() {
        if (vezJogador || verificarFimDeJogo()) return;

        int[] jogada = encontrarMelhorJogada();
        if (jogada != null) {
            fazerJogada(jogada[0], jogada[1], "O");
        }
    }

    private int[] encontrarMelhorJogada() {
        List<int[]> vazias = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (botoes[i][j].getText().equals("")) {
                    vazias.add(new int[]{i, j});
                }
            }
        }
        if (vazias.isEmpty()) return null;

        switch (dificuldade) {
            case "FÁCIL":
                // Jogada totalmente aleatória
                return vazias.get(random.nextInt(vazias.size()));
                
            case "NORMAL":
                // Tenta vencer, depois bloquear, senão aleatório
                int[] vitoria = tentarVencer("O");
                if (vitoria != null) return vitoria;
                int[] bloqueio = tentarBloquear();
                if (bloqueio != null) return bloqueio;
                return vazias.get(random.nextInt(vazias.size()));
                
            case "DIFÍCIL":
                // Estratégia mais inteligente
                int[] vitoriaD = tentarVencer("O");
                if (vitoriaD != null) return vitoriaD;
                int[] bloqueioD = tentarBloquear();
                if (bloqueioD != null) return bloqueioD;
                // Centro primeiro
                if (botoes[1][1].getText().equals("")) return new int[]{1, 1};
                // Cantos
                int[][] cantos = {{0,0}, {0,2}, {2,0}, {2,2}};
                for (int[] canto : cantos) {
                    if (botoes[canto[0]][canto[1]].getText().equals("")) return canto;
                }
                // Bordas
                return vazias.get(random.nextInt(vazias.size()));
        }
        return vazias.get(random.nextInt(vazias.size()));
    }

    private int[] tentarVencer(String jogador) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (botoes[i][j].getText().equals("")) {
                    botoes[i][j].setText(jogador);
                    if (verificarVitoria(jogador)) {
                        botoes[i][j].setText("");
                        return new int[]{i, j};
                    }
                    botoes[i][j].setText("");
                }
            }
        }
        return null;
    }

    private int[] tentarBloquear() {
        return tentarVencer("X"); // Bloquear o jogador
    }

    private boolean verificarVitoria(String jogador) {
        // Verificar linhas
        for (int i = 0; i < 3; i++) {
            if (botoes[i][0].getText().equals(jogador) && 
                botoes[i][1].getText().equals(jogador) && 
                botoes[i][2].getText().equals(jogador)) {
                return true;
            }
        }
        
        // Verificar colunas
        for (int j = 0; j < 3; j++) {
            if (botoes[0][j].getText().equals(jogador) && 
                botoes[1][j].getText().equals(jogador) && 
                botoes[2][j].getText().equals(jogador)) {
                return true;
            }
        }
        
        // Verificar diagonais
        if (botoes[0][0].getText().equals(jogador) && 
            botoes[1][1].getText().equals(jogador) && 
            botoes[2][2].getText().equals(jogador)) {
            return true;
        }
        if (botoes[0][2].getText().equals(jogador) && 
            botoes[1][1].getText().equals(jogador) && 
            botoes[2][0].getText().equals(jogador)) {
            return true;
        }
        
        return false;
    }

    private boolean verificarFimDeJogo() {
        return verificarVitoria("X") || verificarVitoria("O") || jogadas == 9;
    }

    private void verificarVencedor() {
        if (verificarVitoria("X")) {
            vitoriasJogador++;
            statusLabel.setText("🎉 VOCÊ VENCEU!");
            statusLabel.setForeground(Color.GREEN);
            desabilitarBotoes();
        } else if (verificarVitoria("O")) {
            vitoriasIA++;
            statusLabel.setText("🤖 IA VENCEU!");
            statusLabel.setForeground(Color.RED);
            desabilitarBotoes();
            
            // Mensagem especial na primeira derrota
            if (vitoriasIA == 1) {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(this,
                        "💀 VOCÊ É FRACO! MELHORE E VOLTE AQUI! 💀\n\n" +
                        "A IA mostrou quem manda aqui!\n" +
                        "Tente novamente...",
                        "HUMILHAÇÃO DIGITAL",
                        JOptionPane.WARNING_MESSAGE);
                });
            }
        } else if (jogadas == 9) {
            empates++;
            statusLabel.setText("🤝 EMPATE!");
            statusLabel.setForeground(Color.GRAY);
        }
        
        atualizarPlacar();
    }

    private void desabilitarBotoes() {
        for (JButton[] linha : botoes) {
            for (JButton botao : linha) {
                botao.setEnabled(false);
            }
        }
    }

    private void atualizarPlacar() {
        placarLabel.setText("📊 Você:" + vitoriasJogador + " | IA:" + vitoriasIA + " | Empates:" + empates);
    }

    private void reiniciarJogo() {
        for (JButton[] linha : botoes) {
            for (JButton botao : linha) {
                botao.setText("");
                botao.setEnabled(true);
            }
        }
        vezJogador = true; // Jogador sempre começa
        jogadas = 0;
        statusLabel.setText("🎯 SUA VEZ (X)");
        statusLabel.setForeground(Color.BLUE);
    }
}

// JOGO DA MEMÓRIA (mantido funcional)
class JogoMemoriaPanel extends JPanel {
    private GabrielGames parent;
    private JButton[] cartas;
    private int[] valoresCartas;
    private int primeiraCarta = -1, segundaCarta = -1;
    private int paresEncontrados, tentativas;
    private JLabel statusLabel, tentativasLabel;
    private Timer timer;
    private boolean podeClicar = true;
    private String dificuldade;
    private int tempoVirar;

    public JogoMemoriaPanel(GabrielGames parent) {
        this.parent = parent;
        setLayout(new BorderLayout());
        setBackground(new Color(240, 240, 240));
        
        dificuldade = "NORMAL";
        tempoVirar = 1000;
        cartas = new JButton[16];
        valoresCartas = new int[16];

        add(criarCabecalho("🎴 JOGO DA MEMÓRIA"), BorderLayout.NORTH);
        add(criarTabuleiroMemoria(), BorderLayout.CENTER);
        add(criarPainelControle(), BorderLayout.SOUTH);

        iniciarJogo();
    }

    private JPanel criarCabecalho(String titulo) {
        JPanel cabecalho = new JPanel(new BorderLayout());
        cabecalho.setBackground(new Color(180, 70, 130));
        cabecalho.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel lblTitulo = new JLabel(titulo, SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitulo.setForeground(Color.WHITE);

        JButton btnVoltar = new JButton("🏠 Menu");
        btnVoltar.setFont(new Font("Arial", Font.BOLD, 12));
        btnVoltar.addActionListener(e -> parent.mostrarTela("INICIO"));

        cabecalho.add(btnVoltar, BorderLayout.WEST);
        cabecalho.add(lblTitulo, BorderLayout.CENTER);
        return cabecalho;
    }

    private JPanel criarTabuleiroMemoria() {
        JPanel tabuleiroPanel = new JPanel(new GridLayout(4, 4, 5, 5));
        tabuleiroPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        tabuleiroPanel.setBackground(Color.DARK_GRAY);

        for (int i = 0; i < 16; i++) {
            cartas[i] = new JButton("?");
            cartas[i].setFont(new Font("Arial", Font.BOLD, 18));
            cartas[i].setBackground(new Color(70, 130, 180));
            cartas[i].setForeground(Color.WHITE);
            cartas[i].setFocusPainted(false);
            cartas[i].addActionListener(new CartaClickListener(i));
            tabuleiroPanel.add(cartas[i]);
        }
        return tabuleiroPanel;
    }

    private JPanel criarPainelControle() {
        JPanel painel = new JPanel(new BorderLayout());
        painel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        statusLabel = new JLabel("Encontre os pares!", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 16));

        tentativasLabel = new JLabel("Tentativas: 0 | Dificuldade: " + dificuldade, SwingConstants.CENTER);
        tentativasLabel.setFont(new Font("Arial", Font.BOLD, 14));
        tentativasLabel.setForeground(Color.RED);

        JPanel botoesPanel = new JPanel(new GridLayout(1, 3, 5, 0));
        
        JButton btnReiniciar = new JButton("🔄 Novo Jogo");
        JButton btnDificuldade = new JButton("💪 " + dificuldade);
        JButton btnVoltar = new JButton("🏠 Menu");

        btnReiniciar.addActionListener(e -> iniciarJogo());
        btnDificuldade.addActionListener(e -> {
            String[] dificuldades = {"FÁCIL", "NORMAL", "DIFÍCIL"};
            int index = java.util.Arrays.asList(dificuldades).indexOf(dificuldade);
            dificuldade = dificuldades[(index + 1) % dificuldades.length];
            tempoVirar = dificuldade.equals("FÁCIL") ? 1500 : dificuldade.equals("NORMAL") ? 1000 : 700;
            btnDificuldade.setText("💪 " + dificuldade);
            tentativasLabel.setText("Tentativas: " + tentativas + " | Dificuldade: " + dificuldade);
            iniciarJogo();
        });
        btnVoltar.addActionListener(e -> parent.mostrarTela("INICIO"));

        for (JButton btn : new JButton[]{btnReiniciar, btnDificuldade, btnVoltar}) {
            btn.setFont(new Font("Arial", Font.BOLD, 12));
            botoesPanel.add(btn);
        }

        painel.add(statusLabel, BorderLayout.NORTH);
        painel.add(tentativasLabel, BorderLayout.CENTER);
        painel.add(botoesPanel, BorderLayout.SOUTH);
        return painel;
    }

    private void iniciarJogo() {
        paresEncontrados = 0;
        tentativas = 0;
        primeiraCarta = -1;
        segundaCarta = -1;
        podeClicar = true;

        int[] valores = {1,1,2,2,3,3,4,4,5,5,6,6,7,7,8,8};
        Random rand = new Random();
        for (int i = 0; i < valores.length; i++) {
            int randomIndex = rand.nextInt(valores.length);
            int temp = valores[randomIndex];
            valores[randomIndex] = valores[i];
            valores[i] = temp;
        }
        valoresCartas = valores;

        for (int i = 0; i < 16; i++) {
            cartas[i].setText("?");
            cartas[i].setBackground(new Color(70, 130, 180));
            cartas[i].setEnabled(true);
        }

        statusLabel.setText("Encontre os pares!");
        tentativasLabel.setText("Tentativas: 0 | Dificuldade: " + dificuldade);
    }

    private class CartaClickListener implements ActionListener {
        private int index;
        public CartaClickListener(int index) { this.index = index; }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (!podeClicar || cartas[index].getText().equals(String.valueOf(valoresCartas[index]))) return;

            cartas[index].setText(String.valueOf(valoresCartas[index]));
            cartas[index].setBackground(Color.WHITE);

            if (primeiraCarta == -1) {
                primeiraCarta = index;
            } else {
                segundaCarta = index;
                tentativas++;
                tentativasLabel.setText("Tentativas: " + tentativas + " | Dificuldade: " + dificuldade);
                podeClicar = false;

                if (valoresCartas[primeiraCarta] == valoresCartas[segundaCarta]) {
                    paresEncontrados++;
                    statusLabel.setText("Par encontrado! 🎉");
                    cartas[primeiraCarta].setEnabled(false);
                    cartas[segundaCarta].setEnabled(false);
                    cartas[primeiraCarta].setBackground(Color.GREEN);
                    cartas[segundaCarta].setBackground(Color.GREEN);
                    primeiraCarta = -1;
                    segundaCarta = -1;
                    podeClicar = true;

                    if (paresEncontrados == 8) {
                        statusLabel.setText("🎊 VOCÊ VENCEU! 🎊");
                    }
                } else {
                    statusLabel.setText("Tente novamente!");
                    timer = new Timer(tempoVirar, evt -> {
                        cartas[primeiraCarta].setText("?");
                        cartas[primeiraCarta].setBackground(new Color(70, 130, 180));
                        cartas[segundaCarta].setText("?");
                        cartas[segundaCarta].setBackground(new Color(70, 130, 180));
                        primeiraCarta = -1;
                        segundaCarta = -1;
                        podeClicar = true;
                    });
                    timer.setRepeats(false);
                    timer.start();
                }
            }
        }
    }
}