
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import model.Duende;
import model.TreeMapAdaptado;

public class Main {
    public static void main(String[] args) {


        Scanner scanner = new Scanner(System.in);
        System.out.println("Digite o número de duendes:");
        int numDuendes = scanner.nextInt();

        System.out.println("Digite o número de iterações: ");
        int numIteracoes = scanner.nextInt();
        

        TreeMapAdaptado tma = new TreeMapAdaptado();

        for (int i = 0; i < numDuendes; i++) {
            Duende duende = new Duende(i);
            tma.addDuende(duende);
        }

        List<Duende> iterarDuendes = new ArrayList<>(tma.treeMapPrincipal.values());

        for (int i = 0; i < numIteracoes; i++) {
            for (Duende duende : iterarDuendes) {
                double chave = duende.getPosition();
                tma.treeMapPrincipal.remove(chave);
                duende.move();
                tma.addDuende(duende);
                duende.steal(tma.findNearestDuende(duende));
            }
            System.out.println("Iteração " + (i + 1) + " concluída.");
        }

        for (Duende d : iterarDuendes) {
            System.out.println("Duende " + d.getId() + ": " + d.getMoney() + " Dinheiros");
        }


        
        scanner.close();

    }
}
