package com.egyptianExample;

import java.util.*;
import org.json.simple.*;
import org.json.simple.parser.*;

public class EgyptianPyramidsAppExample {

  protected Pharaoh[] pharaohArray;
  protected Pyramid[] pyramidArray;
  protected HashMap<String, Pharaoh> hieroglyphicMap;
  protected HashMap<Integer, Pyramid> pyramidMap;
  protected LinkedHashSet<Integer> requestedPyramidIds;

  public static void main(String[] args) {
    EgyptianPyramidsAppExample app = new EgyptianPyramidsAppExample();
    app.start();
  }

  public void start() {
    Scanner scan = new Scanner(System.in);
    Character command = '_';

    while (command != 'q') {
      printMenu();
      System.out.print("Enter a command: ");
      command = menuGetCommand(scan);

      executeCommand(scan, command);
    }
  }

  public EgyptianPyramidsAppExample() {
    String pharaohFile = "pharaoh.json";
    JSONArray pharaohJSONArray = JSONFile.readArray(pharaohFile);
    initializePharaoh(pharaohJSONArray);

    String pyramidFile = "pyramid.json";
    JSONArray pyramidJSONArray = JSONFile.readArray(pyramidFile);
    initializePyramid(pyramidJSONArray);

    requestedPyramidIds = new LinkedHashSet<>();
    pyramidMap = new HashMap<>();
    for (Pyramid p : pyramidArray) {
      pyramidMap.put(p.id, p);
    }
  }

  private void initializePharaoh(JSONArray pharaohJSONArray) {
    pharaohArray = new Pharaoh[pharaohJSONArray.size()];
    hieroglyphicMap = new HashMap<>();

    for (int i = 0; i < pharaohJSONArray.size(); i++) {
      JSONObject o = (JSONObject) pharaohJSONArray.get(i);

      Integer id = toInteger(o, "id");
      String name = o.get("name").toString();
      Integer begin = toInteger(o, "begin");
      Integer end = toInteger(o, "end");
      Integer contribution = toInteger(o, "contribution");
      String hieroglyphic = o.get("hieroglyphic").toString();

      Pharaoh p = new Pharaoh(id, name, begin, end, contribution, hieroglyphic);
      pharaohArray[i] = p;
      hieroglyphicMap.put(hieroglyphic, p); 
    }
  }

  private void initializePyramid(JSONArray pyramidJSONArray) {
    pyramidArray = new Pyramid[pyramidJSONArray.size()];

    for (int i = 0; i < pyramidJSONArray.size(); i++) {
      JSONObject o = (JSONObject) pyramidJSONArray.get(i);

      Integer id = toInteger(o, "id");
      String name = o.get("name").toString();
      JSONArray contributorsJSONArray = (JSONArray) o.get("contributors");
      String[] contributors = new String[contributorsJSONArray.size()];
      for (int j = 0; j < contributorsJSONArray.size(); j++) {
        String c = contributorsJSONArray.get(j).toString();
        contributors[j] = c;
      }

      Pyramid p = new Pyramid(id, name, contributors);
      pyramidArray[i] = p;
    }
  }

  private Integer toInteger(JSONObject o, String key) {
    String s = o.get(key).toString();
    Integer result = Integer.parseInt(s);
    return result;
  }

  private static Character menuGetCommand(Scanner scan) {
    Character command = '_';
    String rawInput = scan.nextLine();

    if (rawInput.length() > 0) {
      rawInput = rawInput.toLowerCase();
      command = rawInput.charAt(0);
    }
    return command;
  }

  // Command 1
  private void printAllPharaoh() {
    for (int i = 0; i < pharaohArray.length; i++) {
      printMenuLine();
      pharaohArray[i].print();
      printMenuLine();
    }
  }

  // Command 2
  private void displaySpecificPharaoh(Scanner scan) {
    System.out.print("Enter a pharaoh id: ");
    String input = scan.nextLine().trim();

    try {
      int id = Integer.parseInt(input);
      if (id >= 0 && id < pharaohArray.length) {
        printMenuLine();
        pharaohArray[id].print();
        printMenuLine();
      } else {
        System.out.println("ERROR: Pharaoh id " + id + " not found.");
      }
    } catch (NumberFormatException e) {
      System.out.println("ERROR: Please enter a valid integer id.");
    }
  }

  // Command 3
  private void printAllPyramids() {
    for (int i = 0; i < pyramidArray.length; i++) {
      printMenuLine();
      Pyramid pyr = pyramidArray[i];
      System.out.printf("Pyramid: %s (id: %d)\n", pyr.name, pyr.id);
      System.out.println("  Contributors:");
      for (String hieroglyphic : pyr.contributors) {
        Pharaoh pharaoh = hieroglyphicMap.get(hieroglyphic);
        if (pharaoh != null) {
          System.out.printf("    - %s\n", pharaoh.name);
        } else {
          System.out.printf("    - [unknown hieroglyphic: %s]\n", hieroglyphic);
        }
      }
    }
    printMenuLine();
  }

  // Command 4
  private void displaySpecificPyramid(Scanner scan) {
    System.out.print("Enter a pyramid id: ");
    String input = scan.nextLine().trim();

    try {
      int id = Integer.parseInt(input);
      Pyramid found = pyramidMap.get(id);

      if (found == null) {
        System.out.println("ERROR: Pyramid id " + id + " not found.");
        return;
      }

      requestedPyramidIds.add(found.id);

      printMenuLine();
      System.out.printf("Pyramid: %s (id: %d)\n", found.name, found.id);
      System.out.println("  Contributors:");

      int total = 0;
      for (String hieroglyphic : found.contributors) {
        Pharaoh pharaoh = hieroglyphicMap.get(hieroglyphic);
        if (pharaoh != null) {
          System.out.printf("    Name: %-25s  Gold: %d\n",
              pharaoh.name, pharaoh.contribution);
          total += pharaoh.contribution;
        }
      }
      System.out.printf("  Total Contribution: %d gold coins\n", total);
      printMenuLine();

    } catch (NumberFormatException e) {
      System.out.println("ERROR: Please enter a valid integer id.");
    }
  }

  // Command 5
  private void printRequestedPyramids() {
    if (requestedPyramidIds.isEmpty()) {
      System.out.println("No pyramids have been requested yet.");
      return;
    }

    System.out.println("Requested Pyramids Report (no duplicates):");
    for (int pyrId : requestedPyramidIds) {
      Pyramid p = pyramidMap.get(pyrId);
      if (p == null) continue;
      printMenuLine();
      System.out.printf("Pyramid: %s (id: %d)\n", p.name, p.id);
      System.out.println("  Contributors:");
      int total = 0;
      for (String hieroglyphic : p.contributors) {
        Pharaoh pharaoh = hieroglyphicMap.get(hieroglyphic);
        if (pharaoh != null) {
          System.out.printf("    Name: %-25s  Gold: %d\n",
              pharaoh.name, pharaoh.contribution);
          total += pharaoh.contribution;
        }
      }
      System.out.printf("  Total Contribution: %d gold coins\n", total);
    }
    printMenuLine();
  }

  private Boolean executeCommand(Scanner scan, Character command) {
    Boolean success = true;

    switch (command) {
      case '1':
        printAllPharaoh();
        break;
      case '2':
        displaySpecificPharaoh(scan);
        break;
      case '3':
        printAllPyramids();
        break;
      case '4':
        displaySpecificPyramid(scan);
        break;
      case '5':
        printRequestedPyramids();
        break;
      case 'q':
        System.out.println("Thank you for using the Egyptian Pyramid App!");
        break;
      default:
        System.out.println("ERROR: Unknown commmand");
        success = false;
    }

    return success;
  }

  private static void printMenuCommand(Character command, String desc) {
    System.out.printf("%s\t\t%s\n", command, desc);
  }

  private static void printMenuLine() {
    System.out.println(
      "--------------------------------------------------------------------------"
    );
  }

  public static void printMenu() {
    printMenuLine();
    System.out.println("Egyptian Pyramids App");
    printMenuLine();
    System.out.printf("Command\t\tDescription\n");
    System.out.printf("-------\t\t---------------------------------------\n");
    printMenuCommand('1', "List all the pharoahs");
    printMenuCommand('2', "Displays a specific Egyptian pharaoh");
    printMenuCommand('3', "List all the pyramids");
    printMenuCommand('4', "Displays a specific pyramid");
    printMenuCommand('5', "Displays a list of requested pyramids.");
    printMenuCommand('q', "Quit");
    printMenuLine();
  }
}