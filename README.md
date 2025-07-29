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
  - [2.4 Diagramas de Projeto](#24-diagramas-de-projeto)
- [3. Execução do Projeto](#3-execução-do-projeto)
  - [3.1 Setup](#31-setup)
  - [3.2 Execução e configuração da simulação](#32-execução-e-configuração-da-simulação)
- [4. Testes](#4-testes)
  - [4.1 Estrutura de Diretórios de Teste](#41-estrutura-de-diretórios-de-teste)
  - [4.2 Tipos de Teste Implementados](#42-tipos-de-teste-implementados)
  - [4.3 Descrição das Classes de Teste](#43-descrição-das-classes-de-teste)

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
- **Java 21**
- **Maven** para dependências e gerenciamento do projeto
- **Swing** para a interface gráfica
- **JUnit**, **Mockito**, **AssertJ** e **Jqwik** para os testes automatizados

## 2.2 Estrutura do Projeto
A arquitetura do projeto segue um padrão MVC (Model-View-Controller) para melhor organização sem adição desnecessária de complexidade. 

Sendo assim, a estrutura de diretórios dos arquivos _source_ seguem:
```
src/
├── Main.java
├── Controller/
│   ├── SimulationController.java
|   ├── SimulationEngine.java
|   └── SimulationSetup.java
├── model/
|   └── dao/
|       ├── DatabaseManager.java
|       └── UsuarioDAO.java
|   └── datastructure/
│       └── TreeMapAdaptado.java
|   └── entities/
|       └── interfaces/
│           └── EntityOnHorizon.java
│       ├── Cluster.java
│       ├── Duende.java
│       ├── GuardiaoDoHorizonte.java
│       └── Usuario.java
├── resources/
│   └── sprites/
│       └── duende.png
└── view/
    ├── MenuView.java
    └── SimulationView.java
```

Para os arquivos de teste, a estrutura de diretórios é a seguinte:
```
test/
├── controller/
│   ├── SimulationControllerTest.java
│   ├── SimulationEngineTest.java
│   └── SimulationSetupTest.java
├── model/
│   ├── datastructure/
│   │   └── TreeMapAdaptadoTest.java
│   └── entities/
│       ├── ClusterTest.java
│       ├── DuendeTest.java
│       ├── GuardiaoDoHorizonteTest.java
│       └── UsuarioTest.java
```

## 2.3 Paralelismo
A visualização gráfica da simulação é atualizada em tempo real. Para isso, a execução das iterações é feita em uma thread separada, permitindo que a interface gráfica permaneça responsiva. 

A classe `SimulationController` é responsável por gerenciar essa execução paralela, utilizando o método `SwingUtilities.invokeLater()` para que as atualizações na interface gráfica sejam feitas na thread de eventos do Swing.

Para garantir que, ao atingir um critério de parada, a simulação interrompa imediatamente, sobrescrevemos a função `repaint()` da classe `JPanel` e a chamamos dentro do loop de execução, encapsulada pela thread de simulação.

## 2.4 Diagramas de Projeto
Diagrama de Arquitetura:
<img width="4432" height="2788" alt="diagrama-arquitetura" src="https://github.com/user-attachments/assets/5f37edcd-d30d-432e-a242-e9998b2c0d47" />

Diagrama UML de Classes:
<img width="2820" height="2260" alt="diagrama-UML" src="https://github.com/user-attachments/assets/888b9c04-772f-446c-b443-11eb3cada3ef" />

# 3. Execução do Projeto

## 3.1 Setup
Para executar o projeto, siga os passos abaixo:
1. Certifique-se de ter o Java 21 instalado em sua máquina.
2. Clone o repositório:
   ```bash
   git clone https://github.com/eduardopvieira/software-testing
   cd software-testing
    ```
3. Navegue até o diretório do projeto
4. Compile o projeto

## 3.2 Execução e configuração da simulação
1. Execute a `main()` presente em `Main.java`:
2. O menu inicial será exibido, permitindo que você escolha o número de criaturas, o máximo de ouro, e o limite do horizonte.
>Obs.: O máximo de moedas e o limite do horizonte serão critérios de parada da simulação.
3. Clique em "Iniciar Simulação" para começar a simulação.
4. A simulação será exibida em uma nova janela, mostrando as criaturas saltitantes e suas respectivas quantidades de moedas.
5. Ao final da simulação será exibido um resumo do estado final das criaturas.

# 4. Testes

Os testes do projeto foram desenvolvidos com **JUnit 5** e utilizam as bibliotecas **Mockito** para a criação de dublês de teste, **AssertJ** para asserções fluentes, e **Jqwik** para testes baseados em propriedades. Para facilitar a identificação, todos os métodos de teste utilizam a anotação `@DisplayName`.

Os testes foram organizados em diretórios que refletem suas categorias e objetivos, permitindo associar claramente cada classe de teste à estratégia de teste empregada.

## 4.1 Estrutura de Diretórios de Teste
A estrutura de diretórios foi reorganizada para refletir as diferentes categorias de testes aplicadas:

```
test/
├── baseados_em_propriedade/
│   └── UsuarioTest.java
├── dominio/
│   ├── ClusterTest.java
│   └── DuendeTest.java
├── dubles_de_teste/
│   ├── GuardiaoDoHorizonteTest.java
│   ├── SimulationControllerTest.java
│   └── SimulationEngineTest.java
├── estrutural/
│   └── TreeMapAdaptadoTest.java
├── fronteira/
│   └── SimulationSetupTest.java
├── integracao/
│   └── UsuarioDAOTest.java
├── sistema/
│   ├── FluxoSistemaCompletoTest.java
│   ├── ConfigSimulacaoViewObject.java
│   ├── CriarContaDialogObject.java
│   ├── LoginViewObject.java
│   ├── SimulationViewObject.java
│   └── StatisticsViewObject.java
```

## 4.2 Tipos de Teste Implementados

- **Testes de Domínio:** Verificam as regras de negócio e a lógica central das entidades (`DuendeTest`, `ClusterTest`).

- **Testes de Fronteira:** Validam o comportamento do sistema nos limites dos valores de entrada permitidos, como o número mínimo e máximo de duendes (`SimulationSetupTest`).

- **Testes Estruturais:** Focam no funcionamento interno de uma estrutura ou algoritmo, como o tratamento de colisões de posição no `TreeMapAdaptadoTest`.

- **Testes Baseados em Propriedade:** Utilizam a biblioteca **Jqwik** para validar propriedades do sistema com uma vasta gama de dados gerados aleatoriamente, garantindo robustez (`UsuarioTest`).

- **Testes com Dublês:** Empregam **Mockito** para criar dublês (Mocks) que simulam o comportamento de dependências, permitindo testar a lógica de um componente de forma isolada (`SimulationControllerTest`, `SimulationEngineTest`, `GuardiaoDoHorizonteTest`).

- **Testes de Integração:** Verificam a interação entre diferentes camadas da aplicação, especialmente com componentes externos, como a comunicação com o banco de dados em `UsuarioDAOTest`.

- **Testes de Sistema:** Simulam a jornada completa do usuário na aplicação, desde o login até a execução de uma simulação. Utilizam o padrão **Page Object** para interagir com a interface gráfica de forma robusta e legível (`FluxoSistemaCompletoTest`).

## 4.3 Descrição das Classes de Teste

- **`DuendeTest`, `ClusterTest` (Domínio):** Testam o ciclo de vida e as ações das entidades, incluindo a inicialização de atributos, a lógica de movimentação, as interações de roubo e as regras de adição de moedas.

- **`GuardiaoDoHorizonteTest` (Dublês de Teste):** Valida a entidade `Guardiao`, utilizando um mock de `Random` para testar a lógica de movimento de forma determinística.

- **`UsuarioTest` (Baseados em Propriedade):** Valida a entidade `Usuario`, usando testes de propriedade com **Jqwik** para assegurar a robustez do construtor contra uma ampla variedade de dados.

- **`TreeMapAdaptadoTest` (Estrutural):** Focado na estrutura de dados que organiza as entidades. Garante o tratamento de colisões de posição e a eficiência na busca pelo vizinho mais próximo.

- **`SimulationSetupTest` (Fronteira):** Valida a classe que configura o cenário inicial, garantindo que a simulação não inicie com parâmetros inválidos (e.g., número de duendes ou tamanho do horizonte fora dos limites).

- **`SimulationEngineTest` (Dublês de Teste):** Testa o núcleo lógico da simulação. Utiliza mocks para isolar e verificar as condições de término (vitória/derrota) e as regras de interação a cada rodada (colisões, fusões e roubos).

- **`SimulationControllerTest` (Dublês de Teste):** Verifica a camada de controle, testando se o controlador interage corretamente com o DAO para gerenciar a pontuação e as simulações do usuário, de acordo com o resultado da partida.

- **`UsuarioDAOTest` (Integração):** Valida a camada de acesso a dados (DAO), testando a comunicação com um banco de dados em memória (H2) para garantir o funcionamento das operações de CRUD de usuários.

- **`FluxoSistemaCompletoTest` (Sistema):** Orquestra um teste de ponta a ponta que simula a interação real de um usuário. Cobre fluxos como login (com sucesso e falha), criação de conta, visualização de estatísticas e exclusão de conta, garantindo que a aplicação se comporta como esperado do início ao fim.
