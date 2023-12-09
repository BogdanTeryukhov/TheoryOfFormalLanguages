package optimization;

import lombok.extern.slf4j.Slf4j;
import optimization.entity.Tree;
import optimization.utils.DSTR;
import optimization.utils.SSNF;
import org.apache.commons.lang3.SerializationUtils;
import org.springframework.stereotype.Service;
import optimization.utils.ACI;
import optimization.entity.parse.Parser;

import java.util.Scanner;

@Service
public class OptimizationService {
    public String optimization(String regex) {
        Tree tree = Parser.parser(regex);
        Tree ssnfTree = SSNF.ssnf(SerializationUtils.clone(tree));
        Tree associativityTree = ACI.normalizeAssociativity(SerializationUtils.clone(ssnfTree));
        Tree commutativityTree = ACI.normalizeCommutativity(SerializationUtils.clone(associativityTree));
        Tree idempotencyTree = ACI.normalizeIdempotency(SerializationUtils.clone(commutativityTree));
        Tree dstrTree = DSTR.dstrTree(SerializationUtils.clone(idempotencyTree));
        return dstrTree.toString();
    }

    public static void main(String[] args) {
        OptimizationService optimizationService = new OptimizationService();

        Scanner scanner = new Scanner(System.in);
        while (true){
            String regex = scanner.next();
            System.out.println(optimizationService.optimization(regex));

            System.out.println("Do you want to continue? print y/n: ");
            String choice = scanner.next();
            if (choice.equals("n")){
                break;
            }
        }
    }
}
