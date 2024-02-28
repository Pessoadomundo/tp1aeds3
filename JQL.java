import java.util.Scanner;
import java.util.function.Predicate;

public class JQL {
    public static String[] separateQuery(String query){
        String[] res = query.split(" (?=([^\"]*\"[^\"]*\")*[^\"]*$)");
        return res;
    }

    public static Predicate<Produto> and(Predicate<Produto> p1, Predicate<Produto> p2){
        return p -> p1.test(p) && p2.test(p);
    }

    public static Predicate<Produto> or(Predicate<Produto> p1, Predicate<Produto> p2){
        return p -> p1.test(p) || p2.test(p);
    }

    public static Predicate<Produto> getCondition(String property, String operator, String value) throws Exception{
        switch(property){
            case "id":
                return p -> Util.compareNumber(p.getId(), operator, Integer.parseInt(value));
            case "price":
                return p -> Util.compareNumber(p.getPrice(), operator, Double.parseDouble(value));
            case "date":
                return p -> Util.compareNumber(p.getDate(), operator, Util.getUTC(value));
            case "url":
                return p -> p.compareString(property, operator, value);
            case "sku":
                return p -> p.compareString(property, operator, value);
            case "name":
                return p -> p.compareString(property, operator, value);
            case "description":
                return p -> p.compareString(property, operator, value);
            case "currency":
                return p -> p.compareString(property, operator, value);
            case "terms":
                return p -> p.compareString(property, operator, value);
            case "section":
                return p -> Util.compareBoolean(p.getSection(), operator, Boolean.parseBoolean(value));
            case "images":
                return p -> p.compareStringArray(property, value);
            case "image_downloads":
                return p -> p.compareStringArray(property, value);
            default:
                throw new Exception("Invalid property");
        }
    }

    public static Predicate<Produto> getConditionList(String[] query) throws Exception{
        int i = 0;
        String property;
        String operator;
        String value;


        Predicate<Produto> expression = null;
        Predicate<Produto> newExpression;
        int logic = 0; // 0 = nao tem, 1 = and, 2 = or

        while(i<query.length){
            property = query[i];
            operator = query[i+1];
            value = query[i+2];

            newExpression = getCondition(property, operator, value);

            switch (logic) {
                case 0:
                    expression = newExpression;
                    break;
                case 1:
                    expression = and(expression, newExpression);    
                    break;
                case 2:
                    expression = or(expression, newExpression);
                    break;
                default:
                    break;
            }

            if(i < query.length-3){
                if(query[i+3].equals("AND")){
                    i += 4;
                    logic = 1;
                }else if(query[i+3].equals("OR")){
                    i += 4;
                    logic = 2;
                }else{
                    i += 3;
                }
            }else{
                i += 3;
            }
        }

        return expression;

    }

    public static Produto[] query(FileManager fm, String query) throws Exception{
        String[] separated = separateQuery(query);
        int max = 100;
        int conditionLength = separated.length;
        if(separated[separated.length-2].equals("MAX")){
            max = Integer.parseInt(separated[separated.length-1]);
            conditionLength -= 2;
        }

        String[] conditionStr = new String[conditionLength];
        for(int i=0;i<conditionLength;i++){
            conditionStr[i] = separated[i];
        }

        Predicate<Produto> condition = getConditionList(conditionStr);

        Produto[] res = fm.conditionalSearch(condition, max);

        return res;
    }

    public static void startJQL() throws Exception{
        Scanner sc = new Scanner(System.in);
        System.out.println("Qual ação deseja realizar:\n" +
        "1 - Começar novo arquivo\n"+
        "2 - Continuar arquivo existente");

        int action = Integer.parseInt(sc.nextLine());

        FileManager fm = new FileManager();

        if(action == 1){
            System.out.println("Digite o nome do arquivo que deseja criar");
            String filename = sc.nextLine();
            fm.start(filename);
            System.out.println("Digite o nome do arquivo csv que deseja abrir");
            String csv = sc.nextLine();
            fm.loadFromCsv(csv);
        }else{
            System.out.println("Digite o nome do arquivo que deseja abrir");
            String filename = sc.nextLine();
            fm.loadFile(filename);
        }

        int nextAction = 0;

        while(nextAction != 6){
            System.out.println("Qual será a próxima ação?\n" +
            "1 - Pesquisar produtos\n" +
            "2 - Adicionar produtos\n" +
            "3 - Ordenar produtos\n" +
            "4 - Excluir produto\n" +
            "5 - Atualizar produto\n" +
            "6 - Parar");
    
            nextAction = Integer.parseInt(sc.nextLine());
    
            switch(nextAction){
                case 1:
                    System.out.println("Digite a query que deseja realizar");
                    String query = sc.nextLine();
                    Produto[] res = query(fm, query);
                    for(Produto p : res){
                        if(p != null) System.out.println(p.toString() + "\n\n");
                    }
                    break;
                case 2:
                    Produto p = new Produto();
                    System.out.println("Digite o nome do produto");
                    p.setName(sc.nextLine());
                    System.out.println("Digite a descrição do produto");
                    p.setDescription(sc.nextLine());
                    System.out.println("Digite o preço do produto");
                    p.setPrice(sc.nextFloat());
                    System.out.println("Digite a moeda do produto");
                    p.setCurrency(sc.nextLine());
                    System.out.println("Digite a url do produto");
                    p.setUrl(sc.nextLine());
                    System.out.println("Digite o sku do produto");
                    p.setSku(sc.nextLine());
                    System.out.println("Digite a data do produto");
                    p.setDate(sc.nextLong());
                    System.out.println("Digite os termos do produto");
                    p.setTerms(sc.nextLine());
                    System.out.println("Digite a seção do produto");
                    p.setSection(sc.nextBoolean());
                    System.out.println("Digite as imagens do produto");
                    p.setImages(sc.nextLine().split(","));
                    System.out.println("Digite os downloads das imagens do produto");
                    p.setImageDownloads(sc.nextLine().split(","));
                    fm.writeElement(p);

                    break;
                case 3:
                    System.out.println("Escolha a propriedade pela qual deseja ordenar os produtos\n" +
                    "1- id\n" +
                    "2- url\n" +
                    "3- sku\n" +
                    "4- name\n" +
                    "5- description\n" +
                    "6- price\n" +
                    "7- currency\n" +
                    "8- date\n" +
                    "9- terms\n");
                    
                    int choice = Integer.parseInt(sc.nextLine());
                    String property = "";
                    switch(choice){
                        case 1:
                            property = "id";
                            break;
                        case 2:
                            property = "url";
                            break;
                        case 3:
                            property = "sku";
                            break;
                        case 4:
                            property = "name";
                            break;
                        case 5:
                            property = "description";
                            break;
                        case 6:
                            property = "price";
                            break;
                        case 7:
                            property = "currency";
                            break;
                        case 8:
                            property = "date";
                            break;
                        case 9:
                            property = "terms";
                            break;
                        default:
                            break;
                    }
                    
                    String endFile = "";
                    System.out.println("Qual será o arquivo de saída?\n");
                    endFile = sc.nextLine();
                    Sorter.intercalacaoBalanceada(fm, property, endFile);

                    break;
                case 4:
                    System.out.println("Digite o id do produto que deseja excluir");
                    int id = Integer.parseInt(sc.nextLine());
                    fm.deleteElement(id);
                    break;
                case 5:
                    System.out.println("Digite o id do produto que deseja atualizar");
                    int id2 = Integer.parseInt(sc.nextLine());

                    Produto produto = fm.readElement(id2);

                    System.out.println("Qual propriedade deseja atualizar?\n" +
                    "1- id\n" +
                    "2- url\n" +
                    "3- sku\n" +
                    "4- name\n" +
                    "5- description\n" +
                    "6- price\n" +
                    "7- currency\n" +
                    "8- date\n" +
                    "9- terms\n");

                    int choice2 = Integer.parseInt(sc.nextLine());
                    String property2 = "";
                    
                    System.out.println("Digite o novo valor");
                    String value = sc.nextLine();

                    switch(choice2){
                        case 1:
                            property2 = "url";
                            produto.setUrl(value);
                            break;
                        case 2:
                            property2 = "sku";
                            produto.setSku(value);
                            break;
                        case 3:
                            property2 = "name";
                            produto.setName(value);
                            break;
                        case 4:
                            property2 = "description";
                            produto.setDescription(value);
                            break;
                        case 5:
                            property2 = "price";
                            produto.setPrice(Float.parseFloat(value));
                            break;
                        case 6:
                            property2 = "currency";
                            produto.setCurrency(value);
                            break;
                        case 7:
                            property2 = "date";
                            produto.setDate(Long.parseLong(value));
                            break;
                        case 8:
                            property2 = "terms";
                            produto.setTerms(value);
                            break;
                        default:
                            break;
                    }

                    fm.updateElement(produto);
                    
                    break;
                case 6:
                    fm.close();
                    break;
                default:
                    break;
            }
        }
        sc.close();
    }
}
