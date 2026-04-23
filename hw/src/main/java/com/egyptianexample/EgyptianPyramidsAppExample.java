package com.egyptianexample;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Scanner;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

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

  public void start() {
    Scanner scan = new Scanner(System.in);
    Character command = '_';

    while (command != 'q') {
      printMenu();
      System.out.print("Enter a command: ");
      command = menuGetCommand(scan);
      executeCommand(scan, command);
    }

    scan.close();
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
        contributors[j] = contributorsJSONArray.get(j).toString();
      }

      Pyramid p = new Pyramid(id, name, contributors);
      pyramidArray[i] = p;
    }
  }

  private Integer toInteger(JSONObject o, String key) {
    return Integer.parseInt(o.get(key).toString());
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

  private void executeCommand(Scanner scan, Character command) {
    if (command == '1') {
      printAllPharaoh();
    } else if (command == '2') {
      displaySpecificPharaoh(scan);
    } else if (command == '3') {
      printAllPyramids();
    } else if (command == '4') {
      displaySpecificPyramid(scan);
    } else if (command == '5') {
      printRequestedPyramids();
    } else if (command == 'q') {
      System.out.println("Goodbye.");
    } else {
      System.out.println("ERROR: Unknown command.");
    }
  }

  private void printMenu() {
    printMenuLine();
    System.out.println("Nassef's Egyptian Pyramids App");
    printMenuLine();
    System.out.printf("%-10s%-40s\n", "Command", "Description");
    printMenuLine();
    System.out.printf("%-10s%-40s\n", "1", "List all the pharaohs");
    System.out.printf("%-10s%-40s\n", "2", "Displays a specific Egyptian pharaoh");
    System.out.printf("%-10s%-40s\n", "3", "List all the pyramids");
    System.out.printf("%-10s%-40s\n", "4", "Displays a specific pyramid");
    System.out.printf("%-10s%-40s\n", "5", "Displays a list of requested pyramids.");
    System.out.printf("%-10s%-40s\n", "q", "Quit");
    printMenuLine();
  }

  private void printMenuLine() {
    System.out.println("------------------------------------------------------------");
  }

  // Command 1
  private void printAllPharaoh() {
    for (Pharaoh pharaoh : pharaohArray) {
      printMenuLine();
      pharaoh.print();
    }
    printMenuLine();
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
    for (Pyramid pyramid : pyramidArray) {
      printMenuLine();
      System.out.printf("Pyramid %s\n", pyramid.name);
      System.out.printf("\tid: %d\n", pyramid.id);
      System.out.println("\tcontributors:");

      for (String hieroglyphic : pyramid.contributors) {
        Pharaoh pharaoh = hieroglyphicMap.get(hieroglyphic);
        if (pharaoh != null) {
          System.out.printf("\t\t%s\n", pharaoh.name);
        } else {
          System.out.printf("\t\tunknown contributor: %s\n", hieroglyphic);
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
      System.out.printf("Pyramid %s\n", found.name);
      System.out.printf("\tid: %d\n", found.id);
      System.out.println("\tcontributors:");

      int total = 0;

      for (String hieroglyphic : found.contributors) {
        Pharaoh pharaoh = hieroglyphicMap.get(hieroglyphic);

        if (pharaoh != null) {
          System.out.printf(
            "\t\tname: %s, gold: %d\n",
            pharaoh.name,
            pharaoh.contribution
          );
          total += pharaoh.contribution;
        } else {
          System.out.printf("\t\tunknown contributor: %s\n", hieroglyphic);
        }
      }

      System.out.printf("\ttotal contribution: %d gold coins\n", total);
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

    printMenuLine();
    System.out.println("Requested Pyramids Report");
    printMenuLine();

    for (Integer pyramidId : requestedPyramidIds) {
      Pyramid pyramid = pyramidMap.get(pyramidId);

      if (pyramid == null) {
        continue;
      }

      System.out.printf("Pyramid %s\n", pyramid.name);
      System.out.printf("\tid: %d\n", pyramid.id);
      System.out.println("\tcontributors:");

      int total = 0;

      for (String hieroglyphic : pyramid.contributors) {
        Pharaoh pharaoh = hieroglyphicMap.get(hieroglyphic);

        if (pharaoh != null) {
          System.out.printf(
            "\t\tname: %s, gold: %d\n",
            pharaoh.name,
            pharaoh.contribution
          );
          total += pharaoh.contribution;
        } else {
          System.out.printf("\t\tunknown contributor: %s\n", hieroglyphic);
        }
      }

      System.out.printf("\ttotal contribution: %d gold coins\n", total);
      printMenuLine();
    }
  }
}