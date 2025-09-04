package uk.autores.format;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FormatVariablesTest {

    @Test
    void argTypesMatch() {
        {
            var left = FormatExpression.parse("{0}");
            var right = FormatExpression.parse("{0}{0}{0}{0}{0}{0}{0}{0}{0}");
            var actual = FormatVariables.incompatibilities(left, right);
            assertTrue(actual.isEmpty());
        }
        {
            var left = FormatExpression.parse("{0}");
            var right = FormatExpression.parse("{0,number}");
            var actual = FormatVariables.incompatibilities(left, right);
            assertTrue(actual.isEmpty());
        }
        {
            var left = FormatExpression.parse("{0} {1}");
            var right = FormatExpression.parse("{0}");
            var actual = FormatVariables.incompatibilities(left, right);
            assertEquals(1, actual.size());
            assertEquals(1, actual.iterator().next().index());
            assertEquals(FormatVariables.Problem.MISSING, actual.iterator().next().problem());
            assertNotNull(actual.toString());
        }
        {
            var left = FormatExpression.parse("{0}");
            var right = FormatExpression.parse("{1}");
            var actual = FormatVariables.incompatibilities(left, right);
            assertEquals(2, actual.size());
        }
        {
            var left = FormatExpression.parse("{0}");
            var right = FormatExpression.parse("{0} {1} {3}");
            var actual = FormatVariables.incompatibilities(left, right);
            assertEquals(2, actual.size());
            assertEquals(FormatVariables.Problem.NONEXISTENT, actual.iterator().next().problem());
            assertNotNull(actual.toString());
        }
        {
            var left = FormatExpression.parse("{0,number}");
            var right = FormatExpression.parse("{0,date}");
            var actual = FormatVariables.incompatibilities(left, right);
            assertEquals(1, actual.size());
            assertEquals(0, actual.iterator().next().index());
            assertEquals(FormatVariables.Problem.MISMATCH, actual.iterator().next().problem());
            assertNotNull(actual.toString());
        }
        {
            var left = FormatExpression.parse("{0}");
            var right = FormatExpression.parse("{1}");
            var actual = FormatVariables.incompatibilities(left, right);
            assertEquals(2, actual.size());
        }
    }

    @Test
    void typesMatch() {
        {
            var left = FormatExpression.parse("foo {0,number}");
            var right = FormatExpression.parse("{0,number} foo");
            var actual = FormatVariables.incompatibilities(left, right, FormatVariables::typesMatch);
            assertTrue(actual.isEmpty());
        }
        {
            var left = FormatExpression.parse("{0}");
            var right = FormatExpression.parse("{0,number}");
            var actual = FormatVariables.incompatibilities(left, right, FormatVariables::typesMatch);
            assertEquals(1, actual.size());
            assertEquals(0, actual.iterator().next().index());
            assertEquals(FormatVariables.Problem.MISMATCH, actual.iterator().next().problem());
        }
    }
}
