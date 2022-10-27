package org.example;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        String fileName = "/home/residencia/Documentos/atatestepage2.pdf";
        PDDocument document = PDDocument.load(new File(fileName));
        List<Object> coordinates = getCoordinates(document, "RONALDO MEDEIROS DE ARAUJO");
        System.out.println("Na página " + coordinates.get(0) + " e coordenada y:" + coordinates.get(1));

    }

    static List<TextPositionSequence> findWords(PDDocument document, int page, String searchTerm) throws IOException {
        final List<TextPositionSequence> hits = new ArrayList<TextPositionSequence>();
        PDFTextStripper stripper = new PDFTextStripper() {
            @Override
            protected void writeString(String text, List<TextPosition> textPositions) throws IOException {
                TextPositionSequence word = new TextPositionSequence(textPositions);
                String string = word.toString();

                int fromIndex = 0;
                int index;
                while ((index = string.indexOf(searchTerm, fromIndex)) > -1) {
                    hits.add(word.subSequence(index, index + searchTerm.length()));
                    fromIndex = index + 1;
                }
                super.writeString(text, textPositions);
            }
        };

        stripper.setSortByPosition(true);
        stripper.setStartPage(page);
        stripper.setEndPage(page);
        stripper.getText(document);
        return hits;
    }

    static List<Object> getCoordinates(PDDocument document, String searchTerm) throws IOException {
        List<Object> coordinates = new ArrayList<>();
        float participacoesY = 0;
        Integer participacoesPage = 0;
        for (Integer page = 1; page <= document.getNumberOfPages(); page++) {
            List<TextPositionSequence> hits = findWords(document, page, "4. Participações");
            if (hits.size() >= 1) {
                participacoesY = hits.get(0).getY();
                participacoesPage = page;
                break;
            }
        }

        for (Integer page = participacoesPage; page <= document.getNumberOfPages(); page++) {
            List<TextPositionSequence> hits = findWords(document, page, searchTerm);
            if (page.equals(participacoesPage)) {
                for (TextPositionSequence hit : hits) {
                    if (hits.size() >= 1 && hit.getY() >= participacoesY) {
                        coordinates.add(0, page);
                        coordinates.add(1, hit.getY());
                        return coordinates;
                    }
                }
            } else {
                for (TextPositionSequence hit : hits) {
                    if (hits.size() >= 1) {
                        coordinates.add(0, page);
                        coordinates.add(1, hit.getY());
                        return coordinates;
                    }
                }
            }
        }
        return coordinates;
    }

}
