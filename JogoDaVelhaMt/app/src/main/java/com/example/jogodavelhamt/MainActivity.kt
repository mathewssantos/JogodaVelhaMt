package com.example.jogodavelhamt

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.jogodavelhamt.databinding.ActivityMainBinding
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    // Variável para acessar os elementos da UI
    private lateinit var binding: ActivityMainBinding

    // Nosso tabuleiro do jogo, uma matriz que guarda os estados das células
    val tabuleiro = arrayOf(
        arrayOf("", "", ""),
        arrayOf("", "", ""),
        arrayOf("", "", "")
    )

    // Variáveis para controlar o jogo
    var jogadorAtual = "realmadrid" // Começamos com o jogador "realmadrid"
    var dificuldade = "Fácil" // Nível de dificuldade do jogo
    var contraMaquina = false // Se estamos jogando contra a máquina ou outra pessoa

    // Esse método é chamado quando a atividade é criada
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        // Configura os botões e seus ouvintes
        configurarUI()
    }

    // Configura os botões de dificuldade e modo de jogo
    private fun configurarUI() {
        // Botão para selecionar a dificuldade fácil
        findViewById<Button>(R.id.dificuldadeFacil).setOnClickListener {
            dificuldade = "Fácil"
            Toast.makeText(this, "Dificuldade: Fácil", Toast.LENGTH_SHORT).show()
        }

        // Botão para selecionar a dificuldade difícil
        findViewById<Button>(R.id.dificuldadeDificil).setOnClickListener {
            dificuldade = "Difícil"
            Toast.makeText(this, "Dificuldade: Difícil", Toast.LENGTH_SHORT).show()
        }

        // Botão para selecionar o modo contra a máquina
        findViewById<Button>(R.id.modoJogadorVsMaquina).setOnClickListener {
            contraMaquina = true
            Toast.makeText(this, "Você selecionou: Contra o Computador", Toast.LENGTH_SHORT).show()
        }

        // Botão para selecionar o modo contra outro jogador
        findViewById<Button>(R.id.modoJogadorVsJogador).setOnClickListener {
            contraMaquina = false
            Toast.makeText(this, "Você selecionou: Contra o Jogador", Toast.LENGTH_SHORT).show()
        }
    }

    // Função chamada quando um botão do tabuleiro é clicado
    fun buttonClick(view: View) {
        // Converte a view clicada para um botão
        val buttonSelecionado = view as Button

        // Define a imagem do botão com base no jogador atual
        val drawableResource = if (jogadorAtual == "realmadrid") R.drawable.realmadrid else R.drawable.barcelona
        val drawable: Drawable? = getDrawable(drawableResource)
        buttonSelecionado.setBackground(drawable)

        // Atualiza o estado do tabuleiro com base no botão clicado
        atualizarTabuleiro(buttonSelecionado.id)

        // Verifica se há um vencedor
        val vencedor = verificaVencedor(tabuleiro)

        if (!vencedor.isNullOrBlank()) {
            // Se houver um vencedor, exibe uma mensagem e reinicia a atividade
            anunciarVencedor(vencedor)
        } else {
            // Alterna para o próximo jogador
            alternarJogador()
            // Desabilita o botão clicado para evitar cliques repetidos
            buttonSelecionado.isEnabled = false
            // Se for a vez da máquina jogar, realiza a jogada da máquina após um pequeno delay
            if (contraMaquina && jogadorAtual == "barcelona") {
                Handler(Looper.getMainLooper()).postDelayed({
                    jogadaMaquina()
                }, 1000) // Adiciona um delay de 1 segundo
            }
        }
    }

    // Atualiza o tabuleiro com base no botão clicado
    private fun atualizarTabuleiro(buttonId: Int) {
        // Associa o botão clicado à posição correspondente no tabuleiro
        when (buttonId) {
            R.id.buttonZero -> tabuleiro[0][0] = jogadorAtual
            R.id.buttonUm -> tabuleiro[0][1] = jogadorAtual
            R.id.buttonDois -> tabuleiro[0][2] = jogadorAtual
            R.id.buttonTres -> tabuleiro[1][0] = jogadorAtual
            R.id.buttonQuatro -> tabuleiro[1][1] = jogadorAtual
            R.id.buttonCinco -> tabuleiro[1][2] = jogadorAtual
            R.id.buttonSeis -> tabuleiro[2][0] = jogadorAtual
            R.id.buttonSete -> tabuleiro[2][1] = jogadorAtual
            R.id.buttonOito -> tabuleiro[2][2] = jogadorAtual
        }
    }

    // Anuncia o vencedor do jogo e reinicia a atividade
    private fun anunciarVencedor(vencedor: String) {
        Toast.makeText(this, "Vencedor: $vencedor", Toast.LENGTH_LONG).show()
        // Reinicia a atividade para começar um novo jogo
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    // Alterna o jogador atual
    private fun alternarJogador() {
        // Muda o jogador de "realmadrid" para "barcelona" ou vice-versa
        jogadorAtual = if (jogadorAtual == "realmadrid") "barcelona" else "realmadrid"
    }

    // Realiza a jogada da máquina de acordo com a dificuldade selecionada
    private fun jogadaMaquina() {
        if (dificuldade == "Fácil") {
            jogadaFacil()
        } else {
            jogadaDificil()
        }
    }

    // Realiza uma jogada fácil, escolhendo uma célula vazia aleatória
    private fun jogadaFacil() {
        // Cria uma lista de movimentos possíveis (células vazias)
        val movimentosPossiveis = mutableListOf<Pair<Int, Int>>()
        for (i in 0..2) {
            for (j in 0..2) {
                if (tabuleiro[i][j].isEmpty()) {
                    movimentosPossiveis.add(Pair(i, j))
                }
            }
        }

        // Se houver movimentos possíveis, escolhe um aleatório e realiza a jogada
        if (movimentosPossiveis.isNotEmpty()) {
            val movimento = movimentosPossiveis[Random.nextInt(movimentosPossiveis.size)]
            tabuleiro[movimento.first][movimento.second] = jogadorAtual
            // Simula o clique no botão correspondente para atualizar a interface
            executarCliqueBotao(movimento.first, movimento.second)
        }
    }

    // Realiza uma jogada difícil, tentando ganhar ou bloquear o adversário
    private fun jogadaDificil() {
        // Encontra o melhor movimento para a máquina (tentar ganhar ou bloquear o adversário)
        val movimento = melhorMovimento()
        if (movimento != null) {
            tabuleiro[movimento.first][movimento.second] = jogadorAtual
            // Simula o clique no botão correspondente para atualizar a interface
            executarCliqueBotao(movimento.first, movimento.second)
        } else {
            // Se não houver movimentos estratégicos, realiza uma jogada fácil
            jogadaFacil()
        }
    }

    // Encontra o melhor movimento para a máquina
    private fun melhorMovimento(): Pair<Int, Int>? {
        // Percorre todas as células do tabuleiro
        for (i in 0..2) {
            for (j in 0..2) {
                if (tabuleiro[i][j].isEmpty()) {
                    // Tenta ganhar o jogo
                    tabuleiro[i][j] = jogadorAtual
                    if (verificaVencedor(tabuleiro) == jogadorAtual) {
                        tabuleiro[i][j] = ""
                        return Pair(i, j)
                    }
                    tabuleiro[i][j] = ""

                    // Tenta bloquear o adversário
                    tabuleiro[i][j] = if (jogadorAtual == "realmadrid") "barcelona" else "realmadrid"
                    if (verificaVencedor(tabuleiro) == if (jogadorAtual == "realmadrid") "barcelona" else "realmadrid") {
                        tabuleiro[i][j] = ""
                        return Pair(i, j)
                    }
                    tabuleiro[i][j] = ""
                }
            }
        }
        return null
    }

    // Simula um clique no botão correspondente à posição no tabuleiro
    private fun executarCliqueBotao(row: Int, col: Int) {
        when {
            row == 0 && col == 0 -> findViewById<Button>(R.id.buttonZero).performClick()
            row == 0 && col == 1 -> findViewById<Button>(R.id.buttonUm).performClick()
            row == 0 && col == 2 -> findViewById<Button>(R.id.buttonDois).performClick()
            row == 1 && col == 0 -> findViewById<Button>(R.id.buttonTres).performClick()
            row == 1 && col == 1 -> findViewById<Button>(R.id.buttonQuatro).performClick()
            row == 1 && col == 2 -> findViewById<Button>(R.id.buttonCinco).performClick()
            row == 2 && col == 0 -> findViewById<Button>(R.id.buttonSeis).performClick()
            row == 2 && col == 1 -> findViewById<Button>(R.id.buttonSete).performClick()
            row == 2 && col == 2 -> findViewById<Button>(R.id.buttonOito).performClick()
        }
    }

    // Verifica se há um vencedor no tabuleiro
    fun verificaVencedor(tabuleiro: Array<Array<String>>): String? {
        // Verifica as linhas
        for (i in 0 until 3) {
            if (tabuleiro[i][0] == tabuleiro[i][1] && tabuleiro[i][1] == tabuleiro[i][2] && tabuleiro[i][0].isNotEmpty()) {
                return tabuleiro[i][0]
            }
        }

        // Verifica as colunas
        for (i in 0 until 3) {
            if (tabuleiro[0][i] == tabuleiro[1][i] && tabuleiro[1][i] == tabuleiro[2][i] && tabuleiro[0][i].isNotEmpty()) {
                return tabuleiro[0][i]
            }
        }

        // Verifica as diagonais
        if (tabuleiro[0][0] == tabuleiro[1][1] && tabuleiro[1][1] == tabuleiro[2][2] && tabuleiro[0][0].isNotEmpty()) {
            return tabuleiro[0][0]
        }
        if (tabuleiro[0][2] == tabuleiro[1][1] && tabuleiro[1][1] == tabuleiro[2][0] && tabuleiro[0][2].isNotEmpty()) {
            return tabuleiro[0][2]
        }

        // Verifica se há empate (todas as células preenchidas sem vencedor)
        var empate = 0
        for (linha in tabuleiro) {
            for (valor in linha) {
                if (valor == "realmadrid" || valor == "barcelona") {
                    empate++
                }
            }
        }

        // Se todas as células estiverem preenchidas, declara empate
        if (empate == 9) {
            return "Empate"
        }

        // Nenhum vencedor ou empate
        return null
    }
}
