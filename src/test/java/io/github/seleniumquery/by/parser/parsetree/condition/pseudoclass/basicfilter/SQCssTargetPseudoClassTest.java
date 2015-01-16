package io.github.seleniumquery.by.parser.parsetree.condition.pseudoclass.basicfilter;

import org.junit.Test;

import static io.github.seleniumquery.by.parser.parsetree.condition.pseudoclass.PseudoClassTestUtils.assertPseudo;

public class SQCssTargetPseudoClassTest {

    @Test
    public void translate() {
        assertPseudo(":target", SQCssTargetPseudoClass.class);
    }

}