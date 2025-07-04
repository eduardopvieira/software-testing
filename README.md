![saltitantes.png](saltitantes.png)

----

> Saltitantes é uma resolução, em Java, do problema dos duendes saltitantes. Este projeto foi motivado pela disciplina de Teste de Software na UFERSA.

## Índice
- [1. Problema e Requisitos](#1-problema-e-requisitos)
  - [1.1 Requisitos da Simulação](#11-requisitos-da-simulação)
  - [1.2 Requisitos de sistema](#12-requisitos-de-sistema)
- [2. Resolução Proposta](#2-resolução-proposta)
  - [2.1 Tecnologias Usadas](#21-tecnologias-usadas)
  - [2.2 Estrutura do Projeto](#22-estrutura-do-projeto)
  - [2.3 Paralelismo](#23-paralelismo)
- [3. Execução do Projeto](#3-execução-do-projeto)
- [3.1 Setup](#31-setup)
- [3.2 Execução e configuração da simulação](#32-execução-e-configuração-da-simulação)
- [4. Testes](#4-testes)
- [4.1 Classes de teste](#41-classes-de-teste)
- [4.2 Cobertura de Testes](#42-cobertura-de-testes)

# 1. Problema e Requisitos
Implementem, em Java, uma simulação de criaturas saltitantes, conforme requisitos, a seguir:

## 1.1 Requisitos da Simulação
- A simulação compreende $n$ criaturas, numeradas de $1$ a $n$.
- Para cada criatura $i$, $1 ≤ i ≤ n$, a simulação mantém uma quantidade de moedas de ouro $g_i$, cujo valor inicial é um milhão.
- A simulação mantém, ainda, para cada criatura $i$, um lugar no horizonte, que é representado com um número de ponto flutuante de dupla precisão, $x_i$.
- Em cada iteração da simulação, as criaturas são processadas na ordem.
- Crie uma criatura especial, denominada "guardião do horizonte", cujo índice deve ser $n+1$, para n criaturas saltitantes. Para o guardião, a simulação mantém uma quantidade de moedas de ouro $g_n+1$, cujo valor inicial é 0 (zero), e um lugar no horizonte que é representado por um número de ponto flutuante de dupla precisão $x_n+1$.
- O processamento de uma criatura em uma iteração inicia pela computação de um novo lugar no horizonte para $i$, que é determinado por $x_i ← x_i + rg_i$, onde $r$ é um número de ponto flutuante gerado aleatoriamente dentro do intervalo $−1$ e $1$. A criatura $i$ então rouba metade das moedas de ouro da criatura mais próxima de um dos seus lados e adiciona esta quantidade em seu respectivo $g_i$.
- Se o novo lugar computado já estiver ocupado, digamos pela criatura $j$, tais criaturas se unem para formar um cluster$_{ij}$, somando suas moedas de ouro, ou seja, $g_{ij} = g_i + g_j$.
- A cada iteração, o guardião deve ser processado após as $n$ criaturas/clusters existentes naquele momento. A computação de um novo lugar no horizonte para o guardião é idêntico a das criaturas saltitantes (i.e., determinado por $x_n+1 ← x_n+1 + rg_n+1$, onde $r$ é um número de ponto flutuante gerado aleatoriamente dentro do intervalo −1 e 1). Todavia, se o novo lugar computado para o guardião já estiver ocupado por um cluster, digamos o cluster$_{ij}$, tal cluster é eliminado do horizonte e seu respectivo $g_{ij}$ é adicionado ao $g_n+1$, ou seja, $g_n+1 = g_n+1 + g_{ij}$.
- A simulação deve permitir a visualização gráfica de uma série de iterações para um dado número $n$ de criaturas. 
- A simulação é considerada bem-sucedida quando, no horizonte,  restarem apenas o guardião e uma criatura saltitante $i$, sendo que $g_n+1 > g_i$ ou restar apenas o guardião.

## 1.2 Requisitos de sistema
- Forneçam as funcionalidades de inclusão e exclusão de usuários. Para cada usuário, considerem login, senha, avatar e pontuação (quantidade de simulações bem-sucedidas). 
- Forneçam a funcionalidade de estatísticas da simulação, especificamente: pontuação obtida por cada usuário, quantidade de simulações executadas por usuário, quantidade total de simulações, média de simulações bem-sucedidas por usuário e média total de simulações bem-sucedidas. Tal funcionalidade deve ser acessível a todos os usuários cadastrados.

# 2. Resolução Proposta
Esta resolução conta com a implementação de um **treemap** para gerenciar as posições das criaturas, permitindo a busca da criatura mais próxima de forma eficiente. A cada iteração, a posição de cada criatura é atualizada e a quantidade de moedas é ajustada conforme as regras do problema.

Para evitar que o guardião fique preso por começar com 0 moedas, ele é processado com um fator mínimo de 1.000.000 para multiplicar o componente aleatório.

Durante as execuções notou-se que é difícil haver colisões devido ao alto número de posições possíveis no horizonte. Por isso, toda nova posição é limitada à primeira casa decimal, o que aumenta a chance de colisão, e consequentemente, da formação de clusters. Além disso, ao tentar ultrapassar um dos limites do horizonte (inferior ou superior), a criatura tem seu movimento restringido ao limite, garantindo que não saia do horizonte.

## 2.1 Tecnologias Usadas
Para este projeto, foram utilizadas as seguintes tecnologias:
- Java 21 + Maven
- Swing
- JUnit
- Mockito
- AssertJ

## 2.2 Estrutura do Projeto
A arquitetura do projeto segue um padrão MVC (Model-View-Controller) para melhor organização sem adição desnecessária de complexidade. 

Sendo assim, a estrutura de diretórios dos arquivos _source_ seguem:
```
src
├── Main.java
├── Controller
│   └── SimulationController.java
├── datastructure
│   └── TreeMapAdaptado.java
├── model
│   └── Duende.java
├── resources
│   └── sprites
│       └── duende.png
└── view
    ├── MenuView.java
    └── SimulationView.java
```

Para os arquivos de teste, a estrutura de diretórios é a seguinte:
```
test
├── DuendeTest.java
├── SimulationControllerTest.java
└── TreeMapAdaptadoTest.java
```

## 2.3 Paralelismo
A visualização gráfica da simulação é atualizada em tempo real. Para isso, a execução das iterações é feita em uma thread separada, permitindo que a interface gráfica permaneça responsiva. 

A classe `SimulationController` é responsável por gerenciar essa execução paralela, utilizando o método `SwingUtilities.invokeLater()` para que as atualizações na interface gráfica sejam feitas na thread de eventos do Swing.

Para garantir que, ao atingir um critério de parada, a simulação interrompa imediatamente, sobrescrevemos a função `repaint()` da classe `JPanel` e a chamamos dentro do loop de execução, encapsulada pela thread de simulação.

# 3. Execução do Projeto

# 3.1 Setup
Para executar o projeto, siga os passos abaixo:
1. Certifique-se de ter o Java 21 instalado em sua máquina.
2. Clone o repositório:
   ```bash
   git clone https://github.com/eduardopvieira/software-testing
   cd software-testing
    ```
3. Navegue até o diretório do projeto
4. Compile o projeto

# 3.2 Execução e configuração da simulação
1. Execute a `main()` presente em `Main.java`:
2. O menu inicial será exibido, permitindo que você escolha o número de criaturas, o máximo de ouro, e o limite do horizonte.
>Obs.: O máximo de moedas e o limite do horizonte serão critérios de parada da simulação.
3. Clique em "Iniciar Simulação" para começar a simulação.
4. A simulação será exibida em uma nova janela, mostrando as criaturas saltitantes e suas respectivas quantidades de moedas.
5. Ao final da simulação será exibido um resumo do estado final das criaturas.


# 4. Testes
Os testes foram implementados utilizando o JUnit 5. Todos estão localizados no diretório `test/` e incluem:
- Testes unitários
- Testes de pré-condições e pós-condições
- Testes de branchs
- Testes de cobertura.

# 4.1 Classes de teste
Os testes estão organizados em três classes principais:

- `DuendeTest`:
  - Criação e inicialização dos atributos.
  - Movimentação dentro dos limites do horizonte.
  - Operações de roubo e doação de moedas.
  - Validação de pré-condições (como posições e valores inválidos).
  - Testes de borda para posições e moedas.

- `TreeMapAdaptadoTest`:
  - Inserção de duendes em posições distintas e tratamento de colisões de posição.
  - Busca do duende mais próximo, considerando critérios de desempate por riqueza.
  - Validação de pré-condições e pós-condições.
  - Testes de exceção para entradas nulas ou inválidas.
  - Testes de métodos auxiliares como verMaisRico.

- `SimulationControllerTest`:
  - Inicialização da simulação com diferentes parâmetros, incluindo casos inválidos.
  - Criação de duendes e inicialização do mapa.
  - Execução dos métodos principais do controlador, incluindo movimentação, roubo, verificação de chegada e exibição dos resultados.
  - Testes de integração com a interface gráfica (verificação da criação do painel e janela).
  - Testes de métodos utilitários e de controle de fluxo.
  - Cobertura de Testes

# 4.2 Cobertura de Testes
A classe `Duende` atinge 90% de branch coverage, enquanto a `SimulationController` atinge 95% e a `TreeMapAdaptado`, 100%. 

No escopo de `Duende`, com 90% de coverage, existem algumas condições específicas que dependem da aleatoriedade do movimento dos duendes. Estas podem não ser totalmente cobertas em todas as execuções dos testes. Apesar disso, todos os caminhos críticos, validações e exceções são testados explicitamente.

Já no escopo de `SimulationController`, com 95% de coverage, existem condições problemáticas que o código garante que sejam tratadas previamente, e portanto, são impossíveis de darem falsas
(no caso, percorrer uma lista de Duendes que seja vazia/null, mas o código garante que isso não aconteça, e portanto, é impossível de testar). O outro caso acontece na função `verificarChegada()`, que por algum motivo, a coverage alega 30 "true hits", mesmo com a existência de um teste que garante um caso para false hits (o teste `testVerificarChegadaFalso`).
