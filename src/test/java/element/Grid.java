package element;

import driver.Driver;
import org.openqa.selenium.By;

import org.openqa.selenium.WebElement;
import util.TestProperties;


import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

public class Grid {
    Element gridContainer = new Element("gridContainer", By.xpath("to be defined in constructor"));
    Element rowDataHeaders = new Element("headers des lignes de données", By.xpath("//table/thead/tr/th")).setContainer(gridContainer);
    Element rowDataHeader = new Element("header des lignes de données HEADER_NAME", By.xpath("//table/thead/tr/th[contains(., \"HEADER_NAME\")]|//table/thead/tr/th[attribute::*[contains(., \"HEADER_NAME\")]]")).setContainer(gridContainer);
    Element rowDataHeaderSens = new Element("sens header du HEADER_NAME", By.xpath("(//table/thead/tr/th[contains(., \"HEADER_NAME\")]|//table/thead/tr/th[attribute::*[contains(., \"HEADER_NAME\")]])//span[contains(@class, \"ui-sortable-column-icon\")]")).setContainer(gridContainer);
    Element rowDataHeaderByNum = new Element("header des lignes de données HEADER_NUM", By.xpath("//table/thead/tr/th[HEADER_NUM]")).setContainer(gridContainer);
    Element rowDataHeaderSensByNum = new Element("sens header du HEADER_NUM", By.xpath("//table/thead/tr/th[HEADER_NUM]//span[contains(@class, \"ui-sortable-column-icon\")]")).setContainer(gridContainer);
    Element rowDataAll = new Element("liste des lignes de données", By.xpath("//tr[td]")).setContainer(gridContainer);
    Element rowDataByContent = new Element("ligne de données contenant VALUE_IN_ROW", By.xpath("//tr[td][contains(., \"VALUE_IN_ROW\")]")).setContainer(gridContainer);
    Element rowDataByRowNum = new Element("ligne de données ROW_NUMBER", By.xpath("//tr[td][ROW_NUMBER]")).setContainer(gridContainer);
    Element cellDataByRowNumByColNum = new Element("cellule COL_NUMBER de la ligne ROW_NUMBER", By.xpath("//tr[td][ROW_NUMBER]/td[COL_NUMBER]")).setContainer(gridContainer);
    Element cellDataOneColAll = new Element("liste des cellules de la colonne COL_NUMBER", By.xpath("(//tr[td])/td[COL_NUMBER]")).setContainer(gridContainer);
    Element cellActionByContent = new Element("action ATTR_ACTION sur la ligne contenant VALUE_IN_ROW", By.xpath("//tr[td][contains(., \"VALUE_IN_ROW\")]//*[attribute::*[contains(., \"ATTR_ACTION\")]]")).setContainer(gridContainer);
    Element cellActionByRowNum = new Element("action ATTR_ACTION sur la ligne ROW_NUMBER", By.xpath("//tr[td][ROW_NUMBER]//*[attribute::*[contains(., \"ATTR_ACTION\")]]")).setContainer(gridContainer);


    private static HashMap<String, LocalDateTime> dateStartSearch = new HashMap<String, LocalDateTime>();


    private static void startSearch(String from) {
        if (dateStartSearch.containsKey(from)) {
            dateStartSearch.remove(from);
        }
        dateStartSearch.put(from, LocalDateTime.now());
    }
    private static boolean stopSearch(int timeout, String from) {
        if (dateStartSearch.get(from).plusSeconds(timeout).isAfter(LocalDateTime.now())) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Construction d'un objet permettant de gérer les différent WebElement des grilles.
     * @param gridName nom de la grille
     * @param locator locator du container de la grid.
     */
    public Grid(String gridName, By locator) {
        gridContainer.setName(gridName);
        gridContainer.setLocator(locator);
    }

    public Grid setParameter(String[] params) {
        gridContainer.setParameter(params);
        return this;
    }
    public Grid setParameter(String key, String value) {
        gridContainer.setParameter(key, value);
        return this;
    }

    public Element getGridContainer() {
        return 	gridContainer;
    }

    public int getColNumberByHeaderName(String headerName) {
        int correspondingColumn = 0;
        boolean foundCol = false;

        startSearch("getColNumberByHeaderName");
        while (!foundCol && !stopSearch(60, "getColNumberByHeaderName")) {
            try {
                List<WebElement> allHeaders = rowDataHeaders.findAllElements();
                correspondingColumn = 0;
                for (WebElement header : allHeaders) {
                    correspondingColumn += 1;
                    if (header.getAttribute("outerHTML").contains(headerName)) {
                        System.out.println("getColNumberByHeaderName => " + correspondingColumn);
                        foundCol = true;
                        break;
                    }
                }
            } catch (Exception e) {
                System.out.println("column not found " + headerName);
                // on fait rien
            }
        }
        Driver.getReport().log("info", "getColNumberByHeaderName " + headerName + " => " + String.valueOf(correspondingColumn),null,null,null,null);
        return (foundCol?correspondingColumn:0);
    }

    public int getRowNumberBySubstringInRow(String subStringInRow) {
        int correspondingRow = 0;
        boolean foundRow = false;
        startSearch("getRowNumberBySubstringInRow");
        while (!foundRow && !stopSearch(60, "getRowNumberBySubstringInRow")) {
            try {
                List<WebElement> allRows = rowDataAll.findAllElements();
                correspondingRow = 0;
                for (WebElement row : allRows) {
                    correspondingRow += 1;
                    if (row.getAttribute("outerHTML").contains(subStringInRow)) {
                        foundRow = true;
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Driver.getReport().log("info", "getRowNumberBySubstringInRow " + subStringInRow + " => " + String.valueOf(correspondingRow),null,null,null,null);
        return (foundRow?correspondingRow:0);
    }

    public int getRowNumberBySubstringInColumn(String subStringInCell, String headerName) {
        return getRowNumberBySubstringInColumn(subStringInCell, getColNumberByHeaderName(headerName));
    }

    public int getRowNumberBySubstringInColumn(String subStringInCell, int colNumberHeader) {
        int correspondingRow = 0;
        boolean foundRow = false;
        if (colNumberHeader>0) {
            cellDataOneColAll.setParameter("COL_NUMBER", String.valueOf(colNumberHeader));
            startSearch("getRowNumberBySubstringInColumn");
            while (!foundRow && !stopSearch(60, "getRowNumberBySubstringInColumn")) {
                try {
                    List<WebElement> allRows = cellDataOneColAll.findAllElements();
                    for (WebElement row : allRows) {
                        correspondingRow += 1;
                        if (row.getAttribute("outerHTML").contains(subStringInCell)) {
                            System.out.println("getRowNumberBySubstringInColumn row found index : " + correspondingRow);
                            foundRow = true;
                            break;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    // on fait rien
                }
            }
        }
        Driver.getReport().log("info", "getRowNumberBySubstringInColumn " + subStringInCell + " colonne " + String.valueOf(colNumberHeader) + " => " + String.valueOf(correspondingRow),null,null,null,null);
        return (foundRow?correspondingRow:0);
    }
    
    public int[] getCellRowAndColumn(String subStringInRow, String headerName) {
        Driver.getReport().log("info", "getCellRowAndColumn " + subStringInRow + " colonne " + headerName ,null,null,null,null);
        int correspondingColumn = getColNumberByHeaderName(headerName);
        int correspondingRow = getRowNumberBySubstringInRow(subStringInRow);
        return new int[] {correspondingRow, correspondingColumn};
    }

    /**
     * Renvoi le nombre de ligne.
     * @return nombre de ligne
     */
    public int getRowCount() {
        return rowDataAll.getElementsNumber(0);
    }

    /**
     * Indique si la table est displayed.
     * @return true si la table est visible, false sinon
     */
    public boolean exists() {
        return rowDataHeaders.exists(TestProperties.implicit_wait);
    }

    /**
     * Indique si la table contient une valeur dans un délai de timeout.
     * @return true si la table est oui, false sinon
     */
    public boolean contains(String value, int timeout) {
        rowDataByContent.setParameter("VALUE_IN_ROW", value);
        return rowDataByContent.exists(timeout);
    }

    /**
     * Indique si la table contient une valeur dans un délai de TestProperties.implicit_wait.
     * @return true si la table est oui, false sinon
     */
    public boolean contains(String value) {
        rowDataByContent.setParameter("VALUE_IN_ROW", value);
        return rowDataByContent.exists(TestProperties.implicit_wait);
    }

    /**
     * Indique si une ligne de la table contient une valeur dans une colonne.
     * @param subStringInCell valeur que l'on cherche dans la colonne subStringHeaderName
     * @param subStringHeaderName nom de la colonne dans laquelle on cherche subStringInCell
     * @return true si la ligne existe sinon false
     */
    public boolean columnContains(String subStringInCell, String subStringHeaderName) {
        return columnContains(subStringInCell, getColNumberByHeaderName(subStringHeaderName));
    }

    /**
     * Indique si une ligne de la table contient une valeur dans une colonne.
     * @param subStringInCell valeur que l'on cherche dans la colonne subStringHeaderName
     * @param columnNumber numero de la colonne où on recherche la valeur
     * @return true si la ligne existe sinon false
     */
    public boolean columnContains(String subStringInCell, int columnNumber) {
        boolean foundRow = false;
        try {
            if (columnNumber>0) {
                cellDataOneColAll.setParameter("COL_NUMBER", String.valueOf(columnNumber));
                List<WebElement> allRows = cellDataOneColAll.findAllElements();
                for (WebElement row : allRows) {
                    Driver.JSExecutor().executeScript("arguments[0].scrollIntoView(true);", row);
                    if (row.getText().contains(subStringInCell)) {
                        foundRow = true;
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return foundRow;
    }
    /**
     * recupère la valeur d'une cellule de la colonne headerName de la ligne de la table contenant la chaine subStringInRow.
     * @param subStringInRow chaine pour identifier la ligne
     * @param headerName colonne de la cellule dont on veut la valeur
     * @return un tableau de 2 String. String1 = "pass" ou "error". String2 = la valeur si "pass", sinon "row not found" ou "col not found"
     */
    public String getCellsValue(String subStringInRow, String headerName) {
        return getCellsValue(subStringInRow, getColNumberByHeaderName(headerName));
    }

    /**
     * recupère la valeur d'une cellule de la colonne headerName de la ligne de la table contenant la chaine subStringInRow.
     * @param subStringInRow chaine pour identifier la ligne
     * @param columnNumber colonne de la cellule dont on veut la valeur
     * @return un tableau de 2 String. String1 = "pass" ou "error". String2 = la valeur si "pass", sinon "row not found" ou "col not found"
     */
    public String getCellsValue(String subStringInRow, int columnNumber) {
        String value;
        try {
            int rowNumber = getRowNumberBySubstringInRow(subStringInRow);
            if (columnNumber>0) {
                if (rowNumber>0) {
                    cellDataByRowNumByColNum.setParameter(new String[] {"ROW_NUMBER", String.valueOf(rowNumber) , "COL_NUMBER", String.valueOf(columnNumber)});
                    value = cellDataByRowNumByColNum.getValue();
                } else {
                    value = "row not found";
                }
            } else {
                value = "col not found";
            }
        } catch (Exception e) {
            value = e.getMessage();
        }
        Driver.getReport().log("info", "getCellsValue => " + String.valueOf(value),null,null,null,null);
        return value;
    }

    /**
     * indique si la valeur de la colonne headerName de la ligne contenant subStringInRow vaut cellValue.
     * @param subStringInRow chaine pour identifier la ligne
     * @param headerName colonne de la cellule dont on veut la valeur
     * @param cellValue valeur attendue
     * @return true si égalité, false sinon
     */
    public boolean cellsValueEquals(String subStringInRow, String headerName, String cellValue) {
        String value = this.getCellsValue(subStringInRow, headerName);
        if (value.equals(cellValue)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * indique si la valeur de la colonne headerName de la ligne contenant subStringInRow vaut cellValue.
     * @param subStringInRow chaine pour identifier la ligne
     * @param colNumberHeader colonne de la cellule dont on veut la valeur
     * @param cellValue valeur attendue
     * @return true si égalité, false sinon
     */
    public boolean cellsValueEquals(String subStringInRow, int colNumberHeader, String cellValue) {
        String value = this.getCellsValue(subStringInRow, colNumberHeader);
        if (value.equals(cellValue)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * indique si la valeur de la colonne headerName de la ligne contenant subStringInRow contient cellValue.
     * @param subStringInRow chaine pour identifier la ligne
     * @param headerName colonne de la cellule dont on veut la valeur
     * @param cellValue valeur attendue
     * @return true si contenue, false sinon
     */
    public boolean cellsValueContains(String subStringInRow, String headerName, String cellValue) {
        String value = this.getCellsValue(subStringInRow, headerName);
        if (value.trim().contains(cellValue.trim())) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * indique si la valeur de la colonne headerName de la ligne contenant subStringInRow contient cellValue.
     * @param subStringInRow chaine pour identifier la ligne
     * @param colNumberHeader colonne de la cellule dont on veut la valeur
     * @param cellValue valeur attendue
     * @return true si contenue, false sinon
     */
    public boolean cellsValueContains(String subStringInRow, int colNumberHeader, String cellValue) {
        String value = this.getCellsValue(subStringInRow, colNumberHeader);
        if (value.trim().contains(cellValue.trim())) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * recupère la valeur d'une cellule de la colonne cellToReadHeaderName de la ligne de la table dont la colonne subStringHeaderName contient la chaine subStringInCell.
     * @param subStringInCell chaine pour identifier la ligne
     * @param subStringHeaderName colonne de la ligne qui doit contenir subStringInCell
     * @param cellToReadHeaderName colonne de la cellule dont on veut la valeur
     * @return un tableau de 2 String. String1 = "pass" ou "error". String2 = la valeur si "pass", sinon "row not found" ou "col not found"
     */
    public String getCellsValue(String subStringInCell, String subStringHeaderName, String cellToReadHeaderName) {
        return getCellsValue(subStringInCell, getColNumberByHeaderName(subStringHeaderName), getColNumberByHeaderName(cellToReadHeaderName));
    }

    /**
     * recupère la valeur d'une cellule de la colonne cellToReadHeaderName de la ligne de la table dont la colonne subStringHeaderName contient la chaine subStringInCell.
     * @param subStringInCell chaine pour identifier la ligne
     * @param colNumberHeader colonne de la ligne qui doit contenir subStringInCell
     * @param colNumberCellToReadHeader colonne de la cellule dont on veut la valeur
     * @return un tableau de 2 String. String1 = "pass" ou "error". String2 = la valeur si "pass", sinon "row not found" ou "col not found"
     */
    public String getCellsValue(String subStringInCell, int colNumberHeader, int colNumberCellToReadHeader) {
        String value;
        try {
            int correspondingRow = getRowNumberBySubstringInColumn(subStringInCell, colNumberHeader);

            if (colNumberCellToReadHeader>0) {
                if (correspondingRow>0 ) {
                    cellDataByRowNumByColNum.setParameter(new String[] {"ROW_NUMBER", String.valueOf(correspondingRow) , "COL_NUMBER", String.valueOf(colNumberCellToReadHeader)});
                    value = cellDataByRowNumByColNum.getValue();
                } else {
                    value = "row not found";
                }
            } else {
                value = "col not found";
            }
        } catch (Exception e) {
            value = e.getMessage();
        }
        Driver.getReport().log("info", "getCellsValue => " + String.valueOf(value),null,null,null,null);
        return value;
    }


    /**
     * indique si la valeur de cellule de la colonne cellToReadHeaderName de la ligne de la table dont la colonne subStringHeaderName contient la chaine subStringInCell vaut cellValue.
     * @param subStringInCell chaine pour identifier la ligne
     * @param subStringHeaderName colonne de la ligne qui doit contenir subStringInCell
     * @param cellToReadHeaderName colonne de la cellule dont on veut la valeur
     * @param cellValue valeur attendue
     * @return true si égalité, false sinon
     */
    public boolean cellsValueEquals(String subStringInCell, String subStringHeaderName, String cellToReadHeaderName, String cellValue) {
        String value = this.getCellsValue(subStringInCell, subStringHeaderName, cellToReadHeaderName);
        if (value.equals(cellValue)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * indique si la valeur de cellule de la colonne cellToReadHeaderName de la ligne de la table dont la colonne subStringHeaderName contient la chaine subStringInCell vaut cellValue.
     * @param subStringInCell chaine pour identifier la ligne
     * @param colNumberSubStringHeader colonne de la ligne qui doit contenir subStringInCell
     * @param colNumberCellToReadHeader colonne de la cellule dont on veut la valeur
     * @param cellValue valeur attendue
     * @return true si égalité, false sinon
     */
    public boolean cellsValueEquals(String subStringInCell, int colNumberSubStringHeader, int colNumberCellToReadHeader, String cellValue) {
        String value = this.getCellsValue(subStringInCell, colNumberSubStringHeader, colNumberCellToReadHeader);
        if (value.equals(cellValue)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * recupère la valeur de l'attribut attr d'une cellule de la colonne cellToReadHeaderName de la ligne de la table dont la colonne subStringHeaderName contient la chaine subStringInCell.
     * @param subStringInCell chaine pour identifier la ligne
     * @param subStringHeaderName colonne de la ligne qui doit contenir subStringInCell
     * @param cellToReadHeaderName colonne de la cellule dont on veut la valeur
     * @param attr attribut dont on veut la valeur
     * @return un tableau de 2 String. String1 = "pass" ou "error". String2 = la valeur de l'attribut si "pass", sinon "row not found" ou "col not found"
     */
    public String getCellsAttribute(String subStringInCell, String subStringHeaderName, String cellToReadHeaderName, String attr) {
        return getCellsAttribute(subStringInCell, getColNumberByHeaderName(subStringHeaderName), getColNumberByHeaderName(cellToReadHeaderName), attr);
    }

    /**
     * recupère la valeur de l'attribut attr d'une cellule de la colonne cellToReadHeaderName de la ligne de la table dont la colonne subStringHeaderName contient la chaine subStringInCell.
     * @param subStringInCell chaine pour identifier la ligne
     * @param colNumberSubStringHeader colonne de la ligne qui doit contenir subStringInCell
     * @param colNumberCellToReadHeader colonne de la cellule dont on veut la valeur
     * @param attr attribut dont on veut la valeur
     * @return un tableau de 2 String. String1 = "pass" ou "error". String2 = la valeur de l'attribut si "pass", sinon "row not found" ou "col not found"
     */
    public String getCellsAttribute(String subStringInCell, int colNumberSubStringHeader, int colNumberCellToReadHeader, String attr) {
        String value;
        try {
            if (colNumberCellToReadHeader>0) {
                if (colNumberSubStringHeader>0 ) {
                    cellDataByRowNumByColNum.setParameter(new String[] {"ROW_NUMBER", String.valueOf(colNumberSubStringHeader) , "COL_NUMBER", String.valueOf(colNumberCellToReadHeader)});
                    value = cellDataByRowNumByColNum.getAttribute(attr);
                } else {
                    value = "row not found";
                }
            } else {
                value = "col not found";
            }
        } catch (Exception e) {
            value = e.getMessage();
        }
        Driver.getReport().log("info", "getCellsAttribute => " + String.valueOf(value),null,null,null,null);
        return value;
    }


    /////////////////// ACTIONS
    /**
     * Tri ascendant d'une table en fonction d'une colonne.
     * @param header header de la colonne sur laquelle on trie
     */
    public void sortAsc(String header) {
        System.out.println("Table.sortAsc " + header);
        rowDataHeaderSens.setParameter("HEADER_NAME", header);
        rowDataHeader.setParameter("HEADER_NAME", header);
        startSearch("sortAsc");
        while (!rowDataHeaderSens.getAttribute("class").contains("triangle-1-s") && !stopSearch(30, "sortAsc")) {
            rowDataHeader.click();
        }
    }

    /**
     * Tri ascendant d'une table en fonction d'une colonne.
     * @param header header de la colonne sur laquelle on trie
     */
    public void sortAsc(int header) {
        System.out.println("Table.sortAsc " + header);
        rowDataHeaderSensByNum.setParameter("HEADER_NUM", String.valueOf(header));
        rowDataHeaderByNum.setParameter("HEADER_NUM", String.valueOf(header));
        startSearch("sortAsc");
        while (!rowDataHeaderSensByNum.getAttribute("class").contains("triangle-1-s") && !stopSearch(30, "sortAsc")) {
            rowDataHeaderByNum.click();
        }
    }

    /**
     * Tri descendant d'une table en fonction d'une colonne.
     * @param header header de la colonne sur laquelle on trie
     */
    public void sortDesc(String header) {
        System.out.println("Table.sortDesc " + header);
        rowDataHeaderSens.setParameter("HEADER_NAME", header);
        rowDataHeader.setParameter("HEADER_NAME", header);
        startSearch("sortDesc");
        while (!rowDataHeaderSens.getAttribute("class").contains("triangle-1-n") && !stopSearch(30, "sortDesc")) {
            rowDataHeader.click();
        }
    }

    /**
     * Tri descendant d'une table en fonction d'une colonne.
     * @param header header de la colonne sur laquelle on trie
     */
    public void sortDesc(int header) {
        System.out.println("Table.sortDesc " + header);
        rowDataHeaderSensByNum.setParameter("HEADER_NUM", String.valueOf(header));
        rowDataHeaderByNum.setParameter("HEADER_NUM", String.valueOf(header));
        startSearch("sortDesc");
        while (!rowDataHeaderSensByNum.getAttribute("class").contains("triangle-1-n") && !stopSearch(30, "sortDesc")) {
            rowDataHeaderByNum.click();
        }
    }

    /**
     * clique dans la cellule headerName de la première ligne contenant la valeur subStringInRow (la recherche de la chaine n'étant pas limitée à la colonne headerName).
     * Le résultat est tracé dans le rapport.
     * @param subStringInRow valeur que l'on cherche dans une ligne de la table
     * @param headerName nom de la colonne dans laquelle on va cliquer
     * @param pageObjectToBeLoadedAfterClick class pageObjects de la page qui doit être chargée après le click
     * @param assertLoadedMethod nom de la méthode de la class pageObjects qui renvoie true quand la page est chargée après le click
     */
    public void clickCell(String subStringInRow, String headerName, Class pageObjectToBeLoadedAfterClick, String assertLoadedMethod) {
        clickCell(getRowNumberBySubstringInRow(subStringInRow), getColNumberByHeaderName(headerName), pageObjectToBeLoadedAfterClick, assertLoadedMethod);
    }

    /**
     * clique dans la cellule headerName de la première ligne contenant la valeur subStringInRow (la recherche de la chaine n'étant pas limitée à la colonne headerName).
     * Le résultat est tracé dans le rapport.
     * @param subStringInRow valeur que l'on cherche dans une ligne de la table
     * @param colNumberHeader colonne dans laquelle on va cliquer
     * @param pageObjectToBeLoadedAfterClick class pageObjects de la page qui doit être chargée après le click
     * @param assertLoadedMethod nom de la méthode de la class pageObjects qui renvoie true quand la page est chargée après le click
     */
    public void clickCell(String subStringInRow, int colNumberHeader, Class pageObjectToBeLoadedAfterClick, String assertLoadedMethod) {
        clickCell(getRowNumberBySubstringInRow(subStringInRow), colNumberHeader, pageObjectToBeLoadedAfterClick, assertLoadedMethod);
    }

    /**
     * clique dans la cellule headerName de la première ligne contenant la valeur subStringInRow (la recherche de la chaine n'étant pas limitée à la colonne headerName).
     * Le résultat est tracé dans le rapport.
     * @param subStringInRow valeur que l'on cherche dans une ligne de la table
     * @param headerName nom de la colonne dans laquelle on va cliquer
     */
    public void clickCell(String subStringInRow, String headerName) {
        clickCell(subStringInRow, headerName, null, null);
    }

    /**
     * clique dans la cellule headerName de la première ligne contenant la valeur subStringInRow (la recherche de la chaine n'étant pas limitée à la colonne headerName).
     * Le résultat est tracé dans le rapport.
     * @param subStringInRow valeur que l'on cherche dans une ligne de la table
     * @param colNumberHeader nom de la colonne dans laquelle on va cliquer
     */
    public void clickCell(String subStringInRow, int colNumberHeader) {
        clickCell(subStringInRow, colNumberHeader, null, null);
    }

    /**
     * clique dans la cellule headerName de la première ligne contenant la valeur subStringInRow (la recherche de la chaine n'étant pas limitée à la colonne headerName).
     * Le résultat est tracé dans le rapport.
     * @param rowNumber numero de la ligne, commence à 1
     * @param headerName nom de la colonne dans laquelle on va cliquer
     * @param pageObjectToBeLoadedAfterClick class pageObjects de la page qui doit être chargée après le click
     * @param assertLoadedMethod nom de la méthode de la class pageObjects qui renvoie true quand la page est chargée après le click
     */
    public void clickCell(int rowNumber, String headerName, Class pageObjectToBeLoadedAfterClick, String assertLoadedMethod) {
        clickCell(rowNumber, getColNumberByHeaderName(headerName), pageObjectToBeLoadedAfterClick, assertLoadedMethod);
    }

    /**
     * clique dans la cellule headerName de la première ligne contenant la valeur subStringInRow (la recherche de la chaine n'étant pas limitée à la colonne headerName).
     * Le résultat est tracé dans le rapport.
     * @param rowNumber numero de la ligne, commence à 1
     * @param colNumberHeader nom de la colonne dans laquelle on va cliquer
     * @param pageObjectToBeLoadedAfterClick class pageObjects de la page qui doit être chargée après le click
     * @param assertLoadedMethod nom de la méthode de la class pageObjects qui renvoie true quand la page est chargée après le click
     */
    public void clickCell(int rowNumber, int colNumberHeader, Class pageObjectToBeLoadedAfterClick, String assertLoadedMethod) {
        System.out.println("Table.clickCell ");
        String result = "error";
        String errorMessage = "ligne " + rowNumber + " non trouvée";
        try {
            if (colNumberHeader>0) {
                cellDataByRowNumByColNum.setParameter(new String[] {"ROW_NUMBER", String.valueOf(rowNumber) , "COL_NUMBER", String.valueOf(colNumberHeader)});
                if (pageObjectToBeLoadedAfterClick!=null) {
                    cellDataByRowNumByColNum.click(pageObjectToBeLoadedAfterClick, assertLoadedMethod);
                } else {
                    cellDataByRowNumByColNum.click();
                }
                errorMessage = null;
                result = "pass";
            } else {
                errorMessage = "col not found";
            }
        } catch (Exception e) {
            errorMessage = e.getMessage();
        }
        Driver.getReport().log(result, "clickCell " + colNumberHeader + " " + rowNumber, cellDataByRowNumByColNum.getName(), null , null, errorMessage);
    }
    /**
     * clique dans la cellule headerName de la première ligne contenant la valeur subStringInRow (la recherche de la chaine n'étant pas limitée à la colonne headerName).
     * Le résultat est tracé dans le rapport.
     * @param rowNumber numero de la ligne, commence à 1
     * @param headerName nom de la colonne dans laquelle on va cliquer
     */
    public void clickCell(int rowNumber, String headerName) {
        clickCell(rowNumber, headerName, null, null);
    }

    /**
     * Réalise une action sur une ligne de la table contenant une chaine de données subStringInRow. L'action est identifiée par tout ou partie de la valeur d'un attribut de son élément html.
     * Le résultat est tracé dans le rapport.
     * @param correspondingRow numero de la ligne
     * @param action tout ou partie de la valeur d'un attribut de l'élément de l'action (par exemple "common-pencil", "editer")
     * @param pageObjectToBeLoadedAfterClick class pageObjects de la page qui doit être chargée après le click
     * @param assertLoadedMethod nom de la méthode de la class pageObjects qui renvoie true quand la page est chargée après le click
     */
    public void actionOnRow(int correspondingRow, String action, Class pageObjectToBeLoadedAfterClick, String assertLoadedMethod) {
        System.out.println("Table.actionOnRow ");
        String result = "error";
        String errorMessage = "ligne " + correspondingRow + " non trouvée";
        try {
            if (correspondingRow>0) {
                cellActionByRowNum.setParameter(new String[] {"ROW_NUMBER", String.valueOf(correspondingRow), "ATTR_ACTION", action});
                if (pageObjectToBeLoadedAfterClick!=null) {
                    cellActionByRowNum.click(pageObjectToBeLoadedAfterClick, assertLoadedMethod);
                } else {
                    cellActionByRowNum.click();
                }
                result = "pass";
                errorMessage = null;
            }
        } catch (Exception e) {
            errorMessage = e.getMessage();
            System.out.println(errorMessage);
        }
        Driver.getReport().log(result, "actionOnRow " + action + " " + correspondingRow, cellActionByRowNum.getName(), null , null, errorMessage);
    }

    /**
     * Réalise une action sur une ligne de la table contenant une chaine de données subStringInRow. L'action est identifiée par tout ou partie de la valeur d'un attribut de son élément html.
     * Le résultat est tracé dans le rapport.
     * @param correspondingRow numero de la ligne
     * @param action tout ou partie de la valeur d'un attribut de l'élément de l'action (par exemple "common-pencil", "editer")
     */
    public void actionOnRow(int correspondingRow, String action) {
        actionOnRow(correspondingRow, action, null, null);
    }

    /**
     * Réalise une action sur une ligne de la table contenant une chaine de données subStringInRow. L'action est identifiée par tout ou partie de la valeur d'un attribut de son élément html.
     * Le résultat est tracé dans le rapport.
     * @param subStringInRow chaine que la ligne doit contenir
     * @param action tout ou partie de la valeur d'un attribut de l'élément de l'action (par exemple "common-pencil", "editer")
     * @param pageObjectToBeLoadedAfterClick class pageObjects de la page qui doit être chargée après le click
     * @param assertLoadedMethod nom de la méthode de la class pageObjects qui renvoie true quand la page est chargée après le click
     */
    public void actionOnRow(String subStringInRow, String action, Class pageObjectToBeLoadedAfterClick, String assertLoadedMethod) {
        System.out.println("Table.actionOnRow ");
        String result = "error";
        String errorMessage = "ligne " + subStringInRow + " non trouvée";
        try {
            int correspondingRow = getRowNumberBySubstringInRow(subStringInRow);
            if (correspondingRow>0) {
                cellActionByRowNum.setParameter(new String[] {"ROW_NUMBER", String.valueOf(correspondingRow), "ATTR_ACTION", action});
                if (pageObjectToBeLoadedAfterClick!=null) {
                    cellActionByRowNum.click(pageObjectToBeLoadedAfterClick, assertLoadedMethod);
                } else {
                    cellActionByRowNum.click();
                }
                result = "pass";
                errorMessage = null;
            }
        } catch (Exception e) {
            errorMessage = e.getMessage();
            System.out.println(errorMessage);
        }
        Driver.getReport().log(result, "actionOnRow " + action + " " + subStringInRow, cellActionByRowNum.getName(), null , null, errorMessage);
    }

    /**
     * Réalise une action sur une ligne de la table contenant une chaine de données subStringInRow. L'action est identifiée par tout ou partie de la valeur d'un attribut de son élément html.
     * Le résultat est tracé dans le rapport.
     * @param subStringInRow chaine que la ligne doit contenir
     * @param action tout ou partie de la valeur d'un attribut de l'élément de l'action (par exemple "common-pencil", "editer")
     */
    public void actionOnRow(String subStringInRow, String action) {
        actionOnRow(subStringInRow, action, null, null);
    }

    /**
     * Réalise une action sur une ligne de la table contenant une chaine de données subStringInCell dans la colonne subStringHeaderName. L'action est identifiée par tout ou partie de la valeur d'un attribut de son élément html.
     * Le résultat est tracé dans le rapport.
     * @param subStringInCell valeur que l'on cherche dans la colonne subStringHeaderName
     * @param colNumberSubStringHeader nom de la colonne dans laquelle on cherche subStringInCell
     * @param action tout ou partie de la valeur d'un attribut de l'élément de l'action (par exemple "common-pencil", "editer")
     * @param pageObjectToBeLoadedAfterClick class pageObjects de la page qui doit être chargée après le click
     * @param assertLoadedMethod nom de la méthode de la class pageObjects qui renvoie true quand la page est chargée après le click
     */
    public void actionOnRow(String subStringInCell, int colNumberSubStringHeader, String action, Class pageObjectToBeLoadedAfterClick, String assertLoadedMethod) {
        System.out.println("Table.actionOnRow ");
        String result = "error";
        String errorMessage = "ligne " + subStringInCell + " non trouvée";
        try {
            int correspondingRow = getRowNumberBySubstringInColumn(subStringInCell, colNumberSubStringHeader);
            if (correspondingRow>0) {
                cellActionByRowNum.setParameter(new String[] {"ROW_NUMBER", String.valueOf(correspondingRow), "ATTR_ACTION", action});
                if (pageObjectToBeLoadedAfterClick!=null) {
                    cellActionByRowNum.click(pageObjectToBeLoadedAfterClick, assertLoadedMethod);
                } else {
                    cellActionByRowNum.click();
                }
                result = "pass";
                errorMessage = null;
            }
        } catch (Exception e) {
            errorMessage = e.getMessage();
            System.out.println(errorMessage);
        }
        Driver.getReport().log(result, "actionOnRow " + action + " " + subStringInCell, cellActionByRowNum.getName(), null , null, errorMessage);
    }

    /**
     * Réalise une action sur une ligne de la table contenant une chaine de données subStringInCell dans la colonne subStringHeaderName. L'action est identifiée par tout ou partie de la valeur d'un attribut de son élément html.
     * Le résultat est tracé dans le rapport.
     * @param subStringInCell valeur que l'on cherche dans la colonne subStringHeaderName
     * @param subStringHeaderName nom de la colonne dans laquelle on cherche subStringInCell
     * @param action tout ou partie de la valeur d'un attribut de l'élément de l'action (par exemple "common-pencil", "editer")
     * @param pageObjectToBeLoadedAfterClick class pageObjects de la page qui doit être chargée après le click
     * @param assertLoadedMethod nom de la méthode de la class pageObjects qui renvoie true quand la page est chargée après le click
     */
    public void actionOnRow(String subStringInCell, String subStringHeaderName, String action, Class pageObjectToBeLoadedAfterClick, String assertLoadedMethod) {
        actionOnRow(subStringInCell, getColNumberByHeaderName(subStringHeaderName), action, pageObjectToBeLoadedAfterClick, assertLoadedMethod);
    }

    /**
     * Réalise une action sur une ligne de la table contenant une chaine de données subStringInCell dans la colonne subStringHeaderName. L'action est identifiée par tout ou partie de la valeur d'un attribut de son élément html.
     * Le résultat est tracé dans le rapport.
     * @param subStringInCell valeur que l'on cherche dans la colonne subStringHeaderName
     * @param subStringHeaderName nom de la colonne dans laquelle on cherche subStringInCell
     * @param action tout ou partie de la valeur d'un attribut de l'élément de l'action (par exemple "common-pencil", "editer")
     */
    public void actionOnRow(String subStringInCell, String subStringHeaderName, String action) {
        actionOnRow(subStringInCell, subStringHeaderName, action, null, null);
    }

    //////////////////////////////// ASSERTIONS
    /**
     * vérifie qu'une ligne de la table contient une valeur dans une colonne.
     * Le résultat est tracé dans le rapport.
     * @param subStringInRow valeur que l'on cherche dans la colonne subStringHeaderName
     */
    public void assertContains(String subStringInRow) {
        startSearch("assertContains");
        boolean contains = contains(subStringInRow);
        while (!contains && !stopSearch(30,"assertContains")) {
            contains = contains(subStringInRow);
        }
        Driver.getReport().log((contains?"pass":"fail"), "assertContains " + subStringInRow , null, null , null, null);
    }

    /**
     * vérifie qu'aucune ligne de la table ne contient une valeur dans une colonne.
     * Le résultat est tracé dans le rapport.
     * @param subStringInRow valeur que l'on cherche dans la colonne subStringHeaderName
     */
    public void assertNotContains(String subStringInRow) {
        startSearch("asserNotContains");
        boolean contains = contains(subStringInRow);
        while (contains && !stopSearch(30,"asserNotContains")) {
            contains = contains(subStringInRow);
        }
        Driver.getReport().log((!contains?"pass":"fail"), "asserNotContains " + subStringInRow , null, null , null, null);
    }

    /**
     * vérifie que la cellule de la colonne cellToReadHeaderName de la ligne de la table dont la colonne subStringHeaderName contient la chaine subStringInCell contient l'élément element.
     * Le résultat est tracé dans le rapport.
     * @param subStringInCell chaine pour identifier la ligne
     * @param colNumberSubStringHeader colonne de la ligne qui doit contenir subStringInCell
     * @param colNumberCellToReadHeader colonne de la cellule dont on veut la valeur
     * @param element element attendu dans la cellule
     */
    public void assertCellsContainsElement(String subStringInCell, int colNumberSubStringHeader, int colNumberCellToReadHeader, Element element) {
        assertCellsContainsElement( subStringInCell,  colNumberSubStringHeader,  colNumberCellToReadHeader,  element, false);
    }

    /**
     * vérifie que la cellule de la colonne cellToReadHeaderName de la ligne de la table dont la colonne subStringHeaderName contient la chaine subStringInCell contient l'élément element.
     * Le résultat est tracé dans le rapport.
     * @param subStringInCell chaine pour identifier la ligne
     * @param subStringHeaderName colonne de la ligne qui doit contenir subStringInCell
     * @param cellToReadHeaderName colonne de la cellule dont on veut la valeur
     * @param element element attendu dans la cellule
     */
    public void assertCellsContainsElement(String subStringInCell, String subStringHeaderName, String cellToReadHeaderName, Element element) {
        assertCellsContainsElement( subStringInCell,  subStringHeaderName,  cellToReadHeaderName,  element, false);
    }

    /**
     * vérifie que la cellule de la colonne cellToReadHeaderName de la ligne de la table dont la colonne subStringHeaderName contient la chaine subStringInCell contient l'élément element.
     * Le résultat est tracé dans le rapport.
     * @param subStringInCell chaine pour identifier la ligne
     * @param subStringHeaderName colonne de la ligne qui doit contenir subStringInCell
     * @param cellToReadHeaderName colonne de la cellule dont on veut la valeur
     * @param element element attendu dans la cellule
     * @param justWarning true si tracer l'echec en warning, false si tracer l'echec en fail auquel cas le test s'arrête
     */
    public void assertCellsContainsElement(String subStringInCell, String subStringHeaderName, String cellToReadHeaderName, Element element, boolean justWarning) {
        assertCellsContainsElement(subStringInCell, getColNumberByHeaderName(subStringHeaderName), getColNumberByHeaderName(cellToReadHeaderName), element, justWarning);
    }

    /**
     * vérifie que la cellule de la colonne cellToReadHeaderName de la ligne de la table dont la colonne subStringHeaderName contient la chaine subStringInCell contient l'élément element.
     * Le résultat est tracé dans le rapport.
     * @param subStringInCell chaine pour identifier la ligne
     * @param colNumberSubStringHeader colonne de la ligne qui doit contenir subStringInCell
     * @param colNumberCellToReadHeader colonne de la cellule dont on veut la valeur
     * @param element element attendu dans la cellule
     * @param justWarning true si tracer l'echec en warning, false si tracer l'echec en fail auquel cas le test s'arrête
     */
    public void assertCellsContainsElement(String subStringInCell, int colNumberSubStringHeader, int colNumberCellToReadHeader, Element element, boolean justWarning) {
        System.out.println("Table.assertCellsContainsElement ");
        String status = (justWarning ? "warning" : "failnext");
        try {
            int row = getRowNumberBySubstringInColumn(subStringInCell, colNumberSubStringHeader);
            Element cell = cellDataByRowNumByColNum.setParameter(new String[] {"ROW_NUMBER", String.valueOf(row) , "COL_NUMBER", String.valueOf(colNumberCellToReadHeader)});
            element.setContainer(cell);
            if (element.exists(TestProperties.implicit_wait)) {
                status = "pass";
            }
        } catch (Exception e) {
            // on fait rien
        }
        Driver.getReport().log(status, "assertCellsContainsElement " + colNumberCellToReadHeader + " " + subStringInCell, cellDataByRowNumByColNum.getName(), element.getName(), "", null);
    }

    /**
     * vérifie que la valeur de la colonne headerName de la ligne contenant subStringInRow vaut cellValue.
     * Le résultat est tracé dans le rapport, le test s'arrête en cas d'erreur.
     * @param subStringInRow chaine pour identifier la ligne
     * @param colNumberHeader colonne de la cellule dont on veut la valeur
     * @param cellValue valeur attendue
     */
    public void assertCellsValueEquals(String subStringInRow, int colNumberHeader, String cellValue) {
        assertCellsValueEquals(subStringInRow, colNumberHeader, cellValue, false);
    }

    /**
     * vérifie que la valeur de la colonne headerName de la ligne contenant subStringInRow vaut cellValue.
     * Le résultat est tracé dans le rapport, le test s'arrête en cas d'erreur.
     * @param subStringInRow chaine pour identifier la ligne
     * @param headerName colonne de la cellule dont on veut la valeur
     * @param cellValue valeur attendue
     */
    public void assertCellsValueEquals(String subStringInRow, String headerName, String cellValue) {
        assertCellsValueEquals(subStringInRow, headerName, cellValue, false);
    }
    /**
     * vérifie que la valeur de la colonne headerName de la ligne contenant subStringInRow vaut cellValue.
     * Le résultat est tracé dans le rapport.
     * @param subStringInRow chaine pour identifier la ligne
     * @param headerName colonne de la cellule dont on veut la valeur
     * @param cellValue valeur attendue
     * @param justWarning true si tracer l'echec en warning, false si tracer l'echec en fail auquel cas le test s'arrête
     */
    public void assertCellsValueEquals(String subStringInRow, String headerName, String cellValue, boolean justWarning) {
        assertCellsValueEquals(subStringInRow, getColNumberByHeaderName(headerName), cellValue, justWarning);
    }

    /**
     * vérifie que la valeur de la colonne headerName de la ligne contenant subStringInRow vaut cellValue.
     * Le résultat est tracé dans le rapport.
     * @param subStringInRow chaine pour identifier la ligne
     * @param colNumberHeader colonne de la cellule dont on veut la valeur
     * @param cellValue valeur attendue
     * @param justWarning true si tracer l'echec en warning, false si tracer l'echec en fail auquel cas le test s'arrête
     */
    public void assertCellsValueEquals(String subStringInRow, int colNumberHeader, String cellValue, boolean justWarning) {
        System.out.println("Table.assertCellsValueEquals ");
        String status = "pass";
        startSearch("assertCellsValueEquals");
        String value = getCellsValue(subStringInRow, colNumberHeader);
        while (!value.trim().equals(cellValue.trim()) && !stopSearch(30,"assertCellsValueEquals")) {
            value = getCellsValue(subStringInRow, colNumberHeader);
        }
        if (!value.trim().equals(cellValue.trim())) {
            status = (justWarning?"warning":"failnext");
        }
        Driver.getReport().log(status, "assertCellsValueEquals " + colNumberHeader + " " + subStringInRow, cellDataByRowNumByColNum.getName(), cellValue, value, null);
    }

    /**
     * vérifie que valeur de cellule de la colonne cellToReadHeaderName de la ligne de la table dont la colonne subStringHeaderName contient la chaine subStringInCell vaut cellValue.
     * Le résultat est tracé dans le rapport, le test s'arrête en cas d'erreur.
     * @param subStringInCell chaine pour identifier la ligne
     * @param colNumberSubStringHeader colonne de la ligne qui doit contenir subStringInCell
     * @param colNumberCellToReadHeader colonne de la cellule dont on veut la valeur
     * @param cellValue valeur attendue
     */
    public void assertCellsValueEquals(String subStringInCell, int colNumberSubStringHeader, int colNumberCellToReadHeader, String cellValue) {
        assertCellsValueEquals(subStringInCell, colNumberSubStringHeader, colNumberCellToReadHeader, cellValue, false);
    }
    /**
     * vérifie que valeur de cellule de la colonne cellToReadHeaderName de la ligne de la table dont la colonne subStringHeaderName contient la chaine subStringInCell vaut cellValue.
     * Le résultat est tracé dans le rapport, le test s'arrête en cas d'erreur.
     * @param subStringInCell chaine pour identifier la ligne
     * @param subStringHeaderName colonne de la ligne qui doit contenir subStringInCell
     * @param cellToReadHeaderName colonne de la cellule dont on veut la valeur
     * @param cellValue valeur attendue
     */
    public void assertCellsValueEquals(String subStringInCell, String subStringHeaderName, String cellToReadHeaderName, String cellValue) {
        assertCellsValueEquals(subStringInCell, subStringHeaderName, cellToReadHeaderName, cellValue, false);
    }
    /**
     * vérifie que valeur de cellule de la colonne cellToReadHeaderName de la ligne de la table dont la colonne subStringHeaderName contient la chaine subStringInCell vaut cellValue.
     * Le résultat est tracé dans le rapport.
     * @param subStringInCell chaine pour identifier la ligne
     * @param subStringHeaderName colonne de la ligne qui doit contenir subStringInCell
     * @param cellToReadHeaderName colonne de la cellule dont on veut la valeur
     * @param cellValue valeur attendue
     * @param justWarning true si tracer l'echec en warning, false si tracer l'echec en fail auquel cas le test s'arrête
     */
    public void assertCellsValueEquals(String subStringInCell, String subStringHeaderName, String cellToReadHeaderName, String cellValue, boolean justWarning) {
        assertCellsValueEquals(subStringInCell, getColNumberByHeaderName(subStringHeaderName), getColNumberByHeaderName(cellToReadHeaderName), cellValue, justWarning);
    }
    /**
     * vérifie que valeur de cellule de la colonne cellToReadHeaderName de la ligne de la table dont la colonne subStringHeaderName contient la chaine subStringInCell vaut cellValue.
     * Le résultat est tracé dans le rapport.
     * @param subStringInCell chaine pour identifier la ligne
     * @param colNumberSubStringHeader colonne de la ligne qui doit contenir subStringInCell
     * @param colNumberCellToReadHeader colonne de la cellule dont on veut la valeur
     * @param cellValue valeur attendue
     * @param justWarning true si tracer l'echec en warning, false si tracer l'echec en fail auquel cas le test s'arrête
     */
    public void assertCellsValueEquals(String subStringInCell, int colNumberSubStringHeader, int colNumberCellToReadHeader, String cellValue, boolean justWarning) {
        System.out.println("Table.assertCellsValueEquals ");
        String status = "pass";
        startSearch("assertCellsValueEquals");
        String value = getCellsValue(subStringInCell, colNumberSubStringHeader, colNumberCellToReadHeader);
        while (!value.trim().equals(cellValue.trim()) && !stopSearch(30,"assertCellsValueEquals")) {
            value = getCellsValue(subStringInCell, colNumberSubStringHeader, colNumberCellToReadHeader);
        }
        if (!value.trim().equals(cellValue.trim())) {
            status = (justWarning?"warning":"failnext");
        }
        Driver.getReport().log(status, "assertCellsValueEquals " + colNumberCellToReadHeader + " " + subStringInCell, cellDataByRowNumByColNum.getName(), cellValue, value, null);
    }

    /**
     * vérifie que la valeur de la colonne headerName de la ligne contenant subStringInRow contient cellValue.
     * Le résultat est tracé dans le rapport, le test s'arrête en cas d'erreur.
     * @param subStringInRow chaine pour identifier la ligne
     * @param colNumberHeader colonne de la cellule dont on veut la valeur
     * @param cellValue valeur attendue
     */
    public void assertCellsValueContains(String subStringInRow, int colNumberHeader, String cellValue) {
        assertCellsValueContains( subStringInRow,  colNumberHeader,  cellValue, false);
    }
    /**
     * vérifie que la valeur de la colonne headerName de la ligne contenant subStringInRow contient cellValue.
     * Le résultat est tracé dans le rapport, le test s'arrête en cas d'erreur.
     * @param subStringInRow chaine pour identifier la ligne
     * @param headerName colonne de la cellule dont on veut la valeur
     * @param cellValue valeur attendue
     */
    public void assertCellsValueContains(String subStringInRow, String headerName, String cellValue) {
        assertCellsValueContains( subStringInRow,  headerName,  cellValue, false);
    }
    /**
     * vérifie que la valeur de la colonne headerName de la ligne contenant subStringInRow contient cellValue.
     * Le résultat est tracé dans le rapport.
     * @param subStringInRow chaine pour identifier la ligne
     * @param headerName colonne de la cellule dont on veut la valeur
     * @param cellValue valeur attendue
     * @param justWarning true si tracer l'echec en warning, false si tracer l'echec en fail auquel cas le test s'arrête
     */
    public void assertCellsValueContains(String subStringInRow, String headerName, String cellValue, boolean justWarning) {
        assertCellsValueContains(subStringInRow, getColNumberByHeaderName(headerName), cellValue, justWarning);
    }
    /**
     * vérifie que la valeur de la colonne headerName de la ligne contenant subStringInRow contient cellValue.
     * Le résultat est tracé dans le rapport.
     * @param subStringInRow chaine pour identifier la ligne
     * @param colNumberHeader colonne de la cellule dont on veut la valeur
     * @param cellValue valeur attendue
     * @param justWarning true si tracer l'echec en warning, false si tracer l'echec en fail auquel cas le test s'arrête
     */
    public void assertCellsValueContains(String subStringInRow, int colNumberHeader, String cellValue, boolean justWarning) {
        System.out.println("Table.assertCellsValueContains ");
        String status = "pass";
        startSearch("assertCellsValueContains");
        String value = getCellsValue(subStringInRow, colNumberHeader);
        while (!value.contains(cellValue) && !stopSearch(30,"assertCellsValueContains")) {
            value = getCellsValue(subStringInRow, colNumberHeader);
        }
        if (!value.contains(cellValue)) {
            status = (justWarning?"warning":"failnext");
        }
        Driver.getReport().log(status, "assertCellsValueContains " + colNumberHeader + " " + subStringInRow, cellDataByRowNumByColNum.getName(),  cellValue, value, null);
    }

    /**
     * vérifie que valeur de cellule de la colonne cellToReadHeaderName de la ligne de la table dont la colonne subStringHeaderName contient la chaine subStringInCell contient cellValue.
     * Le résultat est tracé dans le rapport, le test s'arrête en cas d'erreur.
     * @param subStringInCell chaine pour identifier la ligne
     * @param subStringHeaderName colonne de la ligne qui doit contenir subStringInCell
     * @param cellToReadHeaderName colonne de la cellule dont on veut la valeur
     * @param cellValue valeur attendue
     */
    public void assertCellsValueContains(String subStringInCell, String subStringHeaderName, String cellToReadHeaderName, String cellValue) {
        assertCellsValueContains( subStringInCell,  subStringHeaderName,  cellToReadHeaderName,  cellValue, false);
    }
    /**
     * vérifie que valeur de cellule de la colonne cellToReadHeaderName de la ligne de la table dont la colonne subStringHeaderName contient la chaine subStringInCell contient cellValue.
     * Le résultat est tracé dans le rapport, le test s'arrête en cas d'erreur.
     * @param subStringInCell chaine pour identifier la ligne
     * @param colNumberSubStringHeader colonne de la ligne qui doit contenir subStringInCell
     * @param colNumberCellToReadHeader colonne de la cellule dont on veut la valeur
     * @param cellValue valeur attendue
     */
    public void assertCellsValueContains(String subStringInCell, int colNumberSubStringHeader, int colNumberCellToReadHeader, String cellValue) {
        assertCellsValueContains( subStringInCell,  colNumberSubStringHeader,  colNumberCellToReadHeader,  cellValue, false);
    }

    /**
     * vérifie que valeur de cellule de la colonne cellToReadHeaderName de la ligne de la table dont la colonne subStringHeaderName contient la chaine subStringInCell contient cellValue.
     * Le résultat est tracé dans le rapport.
     * @param subStringInCell chaine pour identifier la ligne
     * @param subStringHeaderName colonne de la ligne qui doit contenir subStringInCell
     * @param cellToReadHeaderName colonne de la cellule dont on veut la valeur
     * @param cellValue valeur attendue
     * @param justWarning true si tracer l'echec en warning, false si tracer l'echec en fail auquel cas le test s'arrête
     */
    public void assertCellsValueContains(String subStringInCell, String subStringHeaderName, String cellToReadHeaderName, String cellValue, boolean justWarning) {
        assertCellsValueContains(subStringInCell, getColNumberByHeaderName(subStringHeaderName), getColNumberByHeaderName(cellToReadHeaderName), cellValue, justWarning);
    }
    /**
     * vérifie que valeur de cellule de la colonne cellToReadHeaderName de la ligne de la table dont la colonne subStringHeaderName contient la chaine subStringInCell contient cellValue.
     * Le résultat est tracé dans le rapport.
     * @param subStringInCell chaine pour identifier la ligne
     * @param colNumberSubStringHeader colonne de la ligne qui doit contenir subStringInCell
     * @param colNumberCellToReadHeader colonne de la cellule dont on veut la valeur
     * @param cellValue valeur attendue
     * @param justWarning true si tracer l'echec en warning, false si tracer l'echec en fail auquel cas le test s'arrête
     */
    public void assertCellsValueContains(String subStringInCell, int colNumberSubStringHeader, int colNumberCellToReadHeader, String cellValue, boolean justWarning) {
        System.out.println("Table.assertCellsValueContains ");
        String status = "pass";
        startSearch("assertCellsValueContains");
        String value = getCellsValue(subStringInCell, colNumberSubStringHeader, colNumberCellToReadHeader);
        while (!value.contains(cellValue) && !stopSearch(30,"assertCellsValueContains")) {
            value = getCellsValue(subStringInCell, colNumberSubStringHeader, colNumberCellToReadHeader);
        }
        if (!value.contains(cellValue)) {
            status = (justWarning?"warning":"failnext");
        }
        Driver.getReport().log(status, "assertCellsValueContains " + colNumberCellToReadHeader + " " + subStringInCell, cellDataByRowNumByColNum.getName(),  cellValue, value, null);
    }

    /**
     * vérifie que la valeur de la colonne headerName de la ligne contenant subStringInRow ne contient pas cellValue.
     * Le résultat est tracé dans le rapport, le test s'arrête en cas d'erreur.
     * @param subStringInRow chaine pour identifier la ligne
     * @param colNumberHeader colonne de la cellule dont on veut la valeur
     * @param cellValue valeur attendue
     */
    public void assertCellsValueNotContains(String subStringInRow, int colNumberHeader, String cellValue) {
        assertCellsValueNotContains( subStringInRow,  colNumberHeader,  cellValue, false);
    }
    /**
     * vérifie que la valeur de la colonne headerName de la ligne contenant subStringInRow ne contient pas cellValue.
     * Le résultat est tracé dans le rapport, le test s'arrête en cas d'erreur.
     * @param subStringInRow chaine pour identifier la ligne
     * @param headerName colonne de la cellule dont on veut la valeur
     * @param cellValue valeur attendue
     */
    public void assertCellsValueNotContains(String subStringInRow, String headerName, String cellValue) {
        assertCellsValueNotContains( subStringInRow,  headerName,  cellValue, false);
    }
    /**
     * vérifie que la valeur de la colonne headerName de la ligne contenant subStringInRow ne contient pas cellValue.
     * Le résultat est tracé dans le rapport.
     * @param subStringInRow chaine pour identifier la ligne
     * @param headerName colonne de la cellule dont on veut la valeur
     * @param cellValue valeur attendue
     * @param justWarning true si tracer l'echec en warning, false si tracer l'echec en fail auquel cas le test s'arrête
     */
    public void assertCellsValueNotContains(String subStringInRow, String headerName, String cellValue, boolean justWarning) {
        assertCellsValueNotContains(subStringInRow, getColNumberByHeaderName(headerName), cellValue, justWarning);
    }
    /**
     * vérifie que la valeur de la colonne headerName de la ligne contenant subStringInRow ne contient pas cellValue.
     * Le résultat est tracé dans le rapport.
     * @param subStringInRow chaine pour identifier la ligne
     * @param colNumberHeader colonne de la cellule dont on veut la valeur
     * @param cellValue valeur attendue
     * @param justWarning true si tracer l'echec en warning, false si tracer l'echec en fail auquel cas le test s'arrête
     */
    public void assertCellsValueNotContains(String subStringInRow, int colNumberHeader, String cellValue, boolean justWarning) {
        System.out.println("Table.assertCellsValueNotContains ");
        String status = "pass";
        startSearch("assertCellsValueNotContains");
        String value = getCellsValue(subStringInRow, colNumberHeader);
        while (value.contains(cellValue) && !stopSearch(30,"assertCellsValueNotContains")) {
            value = getCellsValue(subStringInRow, colNumberHeader);
        }
        if (value.contains(cellValue)) {
            status = (justWarning?"warning":"failnext");
        }
        Driver.getReport().log(status, "assertCellsValueNotEquals " + colNumberHeader+ " " + subStringInRow, cellDataByRowNumByColNum.getName(),  cellValue, value, null);
    }

    /**
     * vérifie qu'une ligne de la table contient une valeur dans une colonne.
     * Le résultat est tracé dans le rapport.
     * @param subStringInCell valeur que l'on cherche dans la colonne subStringHeaderName
     * @param subStringHeaderName nom de la colonne dans laquelle on cherche subStringInCell
     */
    public void assertContains(String subStringInCell, String subStringHeaderName) {
        assertContains(subStringInCell, getColNumberByHeaderName(subStringHeaderName));
    }
    /**
     * vérifie qu'une ligne de la table contient une valeur dans une colonne.
     * Le résultat est tracé dans le rapport.
     * @param subStringInCell valeur que l'on cherche dans la colonne subStringHeaderName
     * @param colNumberSubStringHeader nom de la colonne dans laquelle on cherche subStringInCell
     */
    public void assertContains(String subStringInCell, int colNumberSubStringHeader) {
        startSearch("assertContains");
        boolean contains = columnContains(subStringInCell, colNumberSubStringHeader);
        while (!contains && !stopSearch(30,"assertContains")) {
            contains = columnContains(subStringInCell, colNumberSubStringHeader);
        }
        Driver.getReport().log((contains?"pass":"fail"), "assertContains " + subStringInCell , String.valueOf(colNumberSubStringHeader), null , null, null);
    }

    /**
     * vérifie qu'aucune ligne de la table ne contient une valeur dans une colonne.
     * Le résultat est tracé dans le rapport.
     * @param subStringInCell valeur que l'on cherche dans la colonne subStringHeaderName
     * @param subStringHeaderName nom de la colonne dans laquelle on cherche subStringInCell
     */
    public void assertNotContains(String subStringInCell, String subStringHeaderName) {
        assertNotContains(subStringInCell, getColNumberByHeaderName(subStringHeaderName));
    }
    /**
     * vérifie qu'aucune ligne de la table ne contient une valeur dans une colonne.
     * Le résultat est tracé dans le rapport.
     * @param subStringInCell valeur que l'on cherche dans la colonne subStringHeaderName
     * @param colNumberSubStringHeader nom de la colonne dans laquelle on cherche subStringInCell
     */
    public void assertNotContains(String subStringInCell, int colNumberSubStringHeader) {
        startSearch("asserNotContains");
        boolean contains = columnContains(subStringInCell, colNumberSubStringHeader);
        while (contains && !stopSearch(30,"asserNotContains")) {
            contains = columnContains(subStringInCell, colNumberSubStringHeader);
        }
        Driver.getReport().log((!contains?"pass":"fail"), "asserNotContains " + subStringInCell , String.valueOf(colNumberSubStringHeader), null , null, null);
    }

    /**
     * vérifie que la valeur de l'attribut attr d'une cellule de la colonne cellToReadHeaderName de la ligne de la table dont la colonne subStringHeaderName contient la chaine subStringInCell.
     * Le résultat est tracé dans le rapport, le test s'arrête en cas d'erreur.
     * @param subStringInCell chaine pour identifier la ligne
     * @param colNumberSubStringHeader colonne de la ligne qui doit contenir subStringInCell
     * @param colNumberCellToReadHeader colonne de la cellule dont on veut la valeur
     * @param attr attribut dont on veut la valeur
     * @param attributeValue tout ou partie de la valeur attendu de l'attribut
     */
    public void assertCellsAttributeContains(String subStringInCell, int colNumberSubStringHeader, int colNumberCellToReadHeader, String attr, String attributeValue) {
        assertCellsAttributeContains( subStringInCell,  colNumberSubStringHeader,  colNumberCellToReadHeader,  attr,  attributeValue, false);
    }
    /**
     * vérifie que la valeur de l'attribut attr d'une cellule de la colonne cellToReadHeaderName de la ligne de la table dont la colonne subStringHeaderName contient la chaine subStringInCell.
     * Le résultat est tracé dans le rapport, le test s'arrête en cas d'erreur.
     * @param subStringInCell chaine pour identifier la ligne
     * @param subStringHeaderName colonne de la ligne qui doit contenir subStringInCell
     * @param cellToReadHeaderName colonne de la cellule dont on veut la valeur
     * @param attr attribut dont on veut la valeur
     * @param attributeValue tout ou partie de la valeur attendu de l'attribut
     */
    public void assertCellsAttributeContains(String subStringInCell, String subStringHeaderName, String cellToReadHeaderName, String attr, String attributeValue) {
        assertCellsAttributeContains( subStringInCell,  subStringHeaderName,  cellToReadHeaderName,  attr,  attributeValue, false);
    }
    /**
     * vérifie que la valeur de l'attribut attr d'une cellule de la colonne cellToReadHeaderName de la ligne de la table dont la colonne subStringHeaderName contient la chaine subStringInCell.
     * Le résultat est tracé dans le rapport.
     * @param subStringInCell chaine pour identifier la ligne
     * @param subStringHeaderName colonne de la ligne qui doit contenir subStringInCell
     * @param cellToReadHeaderName colonne de la cellule dont on veut la valeur
     * @param attr attribut dont on veut la valeur
     * @param attributeValue tout ou partie de la valeur attendu de l'attribut
     * @param justWarning true si tracer l'echec en warning, false si tracer l'echec en fail auquel cas le test s'arrête
     */
    public void assertCellsAttributeContains(String subStringInCell, String subStringHeaderName, String cellToReadHeaderName, String attr, String attributeValue, boolean justWarning) {
        assertCellsAttributeContains(subStringInCell, getColNumberByHeaderName(subStringHeaderName), getColNumberByHeaderName(cellToReadHeaderName), attr, attributeValue, justWarning);
    }
    /**
     * vérifie que la valeur de l'attribut attr d'une cellule de la colonne cellToReadHeaderName de la ligne de la table dont la colonne subStringHeaderName contient la chaine subStringInCell.
     * Le résultat est tracé dans le rapport.
     * @param subStringInCell chaine pour identifier la ligne
     * @param colNumberSubStringHeader colonne de la ligne qui doit contenir subStringInCell
     * @param colNumberCellToReadHeader colonne de la cellule dont on veut la valeur
     * @param attr attribut dont on veut la valeur
     * @param attributeValue tout ou partie de la valeur attendu de l'attribut
     * @param justWarning true si tracer l'echec en warning, false si tracer l'echec en fail auquel cas le test s'arrête
     */
    public void assertCellsAttributeContains(String subStringInCell, int colNumberSubStringHeader, int colNumberCellToReadHeader, String attr, String attributeValue, boolean justWarning) {
        System.out.println("Table.assertCellsAttributeContains ");
        String status = "pass";
        startSearch("assertCellsAttributeContains");
        String value = getCellsAttribute(subStringInCell, colNumberSubStringHeader, colNumberCellToReadHeader, attr);
        while (!value.contains(attributeValue) && !stopSearch(30,"assertCellsAttributeContains")) {
            value = getCellsAttribute(subStringInCell, colNumberSubStringHeader, colNumberCellToReadHeader, attr);
        }
        if (!value.contains(attributeValue)) {
            status = (justWarning?"warning":"failnext");
        }
        Driver.getReport().log(status, "assertCellsAttributeContains " + colNumberCellToReadHeader + " " + subStringInCell, cellDataByRowNumByColNum.getName(),  attributeValue, value, null);
    }

    /**
     * vérifie que la valeur de l'attribut attr d'une cellule de la colonne cellToReadHeaderName de la ligne de la table dont la colonne subStringHeaderName ne contient pas la chaine subStringInCell.
     * Le résultat est tracé dans le rapport, le test s'arrête en cas d'erreur.
     * @param subStringInCell chaine pour identifier la ligne
     * @param colNumberSubStringHeader colonne de la ligne qui doit contenir subStringInCell
     * @param colNumberCellToReadHeader colonne de la cellule dont on veut la valeur
     * @param attr attribut dont on veut la valeur
     * @param attributeValue sous-chaine non attendue dans la valeur de l'attribut
     */
    public void assertCellsAttributeNotContains(String subStringInCell, int colNumberSubStringHeader, int colNumberCellToReadHeader, String attr, String attributeValue) {
        assertCellsAttributeNotContains( subStringInCell,  colNumberSubStringHeader,  colNumberCellToReadHeader,  attr,  attributeValue, false);
    }
    /**
     * vérifie que la valeur de l'attribut attr d'une cellule de la colonne cellToReadHeaderName de la ligne de la table dont la colonne subStringHeaderName ne contient pas la chaine subStringInCell.
     * Le résultat est tracé dans le rapport, le test s'arrête en cas d'erreur.
     * @param subStringInCell chaine pour identifier la ligne
     * @param subStringHeaderName colonne de la ligne qui doit contenir subStringInCell
     * @param cellToReadHeaderName colonne de la cellule dont on veut la valeur
     * @param attr attribut dont on veut la valeur
     * @param attributeValue sous-chaine non attendue dans la valeur de l'attribut
     */
    public void assertCellsAttributeNotContains(String subStringInCell, String subStringHeaderName, String cellToReadHeaderName, String attr, String attributeValue) {
        assertCellsAttributeNotContains( subStringInCell,  subStringHeaderName,  cellToReadHeaderName,  attr,  attributeValue, false);
    }
    /**
     * vérifie que la valeur de l'attribut attr d'une cellule de la colonne cellToReadHeaderName de la ligne de la table dont la colonne subStringHeaderName ne contient pas la chaine subStringInCell.
     * Le résultat est tracé dans le rapport.
     * @param subStringInCell chaine pour identifier la ligne
     * @param subStringHeaderName colonne de la ligne qui doit contenir subStringInCell
     * @param cellToReadHeaderName colonne de la cellule dont on veut la valeur
     * @param attr attribut dont on veut la valeur
     * @param attributeValue sous-chaine non attendue dans la valeur de l'attribut
     * @param justWarning true si tracer l'echec en warning, false si tracer l'echec en fail auquel cas le test s'arrête
     */
    public void assertCellsAttributeNotContains(String subStringInCell, String subStringHeaderName, String cellToReadHeaderName, String attr, String attributeValue, boolean justWarning) {
        assertCellsAttributeNotContains(subStringInCell, getColNumberByHeaderName(subStringHeaderName), getColNumberByHeaderName(cellToReadHeaderName), attr, attributeValue, justWarning);
    }
    /**
     * vérifie que la valeur de l'attribut attr d'une cellule de la colonne cellToReadHeaderName de la ligne de la table dont la colonne subStringHeaderName ne contient pas la chaine subStringInCell.
     * Le résultat est tracé dans le rapport.
     * @param subStringInCell chaine pour identifier la ligne
     * @param colNumberSubStringHeader colonne de la ligne qui doit contenir subStringInCell
     * @param colNumberCellToReadHeader colonne de la cellule dont on veut la valeur
     * @param attr attribut dont on veut la valeur
     * @param attributeValue sous-chaine non attendue dans la valeur de l'attribut
     * @param justWarning true si tracer l'echec en warning, false si tracer l'echec en fail auquel cas le test s'arrête
     */
    public void assertCellsAttributeNotContains(String subStringInCell, int colNumberSubStringHeader, int colNumberCellToReadHeader, String attr, String attributeValue, boolean justWarning) {
        System.out.println("Table.assertCellsAttributeNotContains ");
        String status = "pass";
        startSearch("assertCellsAttributeNotContains");
        String value = getCellsAttribute(subStringInCell,colNumberSubStringHeader, colNumberCellToReadHeader, attr);
        while (value.contains(attributeValue) && !stopSearch(30,"assertCellsAttributeNotContains")) {
            value = getCellsAttribute(subStringInCell,colNumberSubStringHeader, colNumberCellToReadHeader, attr);
        }
        if (value.contains(attributeValue)) {
            status = (justWarning?"warning":"failnext");
        }
        Driver.getReport().log(status, "assertCellsAttributeNotContains " + colNumberCellToReadHeader+ " " + subStringInCell, cellDataByRowNumByColNum.getName(),  attributeValue, value, null);
    }

    /**
     * Verifie le nombre de ligne.
     */
    public void assertRowCount(int nbRow) {
        int i = 0;
        int rowCount = getRowCount();
        while (i<10 && nbRow!=rowCount) {rowCount = getRowCount();i++;}
        Driver.getReport().assertEquals("vérification nombre de ligne dans la table ", nbRow, rowCount);
    }
}