
import java.util.Scanner;

import model.Duende;
import model.TreeMapAdaptado;

public class Main {
    public static void main(String[] args) {

        final int NUM_ITERACOES = 10;

        Scanner scanner = new Scanner(System.in);
        System.out.println("Digite o número de duendes:");
        int numDuendes = scanner.nextInt();
        

        TreeMapAdaptado tma = new TreeMapAdaptado();

        for (int i = 0; i < numDuendes; i++) {
            Duende duende = new Duende(i);
            tma.addDuende(duende);
        }


        for (int i = 0; i < NUM_ITERACOES; i++) {
            for (Duende duende : tma.treeMapPrincipal.values()) {
                double chave = duende.getPosition();
                tma.treeMapPrincipal.remove(chave);
                duende.move();
                tma.addDuende(duende);
                duende.steal(tma.findNearestDuende(duende));
            }
            System.out.println("Iteração " + (i + 1) + " concluída.");
        }



        // for (int i = 0; i < NUM_ITERACOES; i++) {
        //     for (Duende duende : tma.treeMapPrincipal.values()) {
        //         double chave = duende.getPosition();
        //         tma.treeMapPrincipal.remove(chave);
        //         duende.move();
        //         tma.addDuende(duende);
        //     }
        //     System.out.println("Iteração " + (i + 1) + " concluída.");
        // }


        
        scanner.close();

    }
}
