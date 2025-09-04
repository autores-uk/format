package uk.autores.format;

import java.util.HashSet;
import java.util.Set;
import java.util.function.BiPredicate;

/**
 * Utility type for working with variables.
 *
 * @since 17.2.0
 */
public final class FormatVariables {
    private FormatVariables() {}

    /**
     * Lax test for {@link FormatVariable} compatibility.
     *
     * @param reference variable to check against
     * @param candidate possible source of incompatibilities
     * @return true if compatible
     */
    public static boolean argTypesMatch(FormatVariable reference, FormatVariable candidate) {
        Class<?> r = reference.type().argType();
        if (r == Object.class) {
            return true;
        }
        Class<?> c = candidate.type().argType();
        return c == Object.class
                || c == r;
    }

    /**
     * Strict test for {@link FormatVariable} compatibility.
     *
     * @param reference variable to check against
     * @param candidate possible source of incompatibilities
     * @return true if compatible
     */
    public static boolean typesMatch(FormatVariable reference, FormatVariable candidate) {
        return reference.type() == candidate.type();
    }

    /**
     * Lax test for {@link FormatExpression} compatibility.
     * Equivalent to <code>var results = incompatibilities(reference, candidate, FormatVariables::argTypesMatch);</code>.
     *
     * @param reference expression to check against
     * @param candidate possible source of incompatibilities
     * @return incompatibilities
     */
    public static Set<Incompatibility> incompatibilities(FormatExpression reference, FormatExpression candidate) {
        return incompatibilities(reference, candidate, FormatVariables::argTypesMatch);
    }

    /**
     * Tests two {@link FormatExpression}s for variable compatibility.
     * Intended to verify that two expressions can take the same format arguments.
     *
     * @param reference expression to check against
     * @param candidate possible source of incompatibilities
     * @param compatible predicate for checking compatibility
     * @return incompatibilities
     */
    public static Set<Incompatibility> incompatibilities(FormatExpression reference, FormatExpression candidate,
                                                         BiPredicate<FormatVariable, FormatVariable> compatible) {
        Set<Incompatibility> results = Set.of();
        for (var f : candidate) {
            if (f instanceof FormatVariable v) {
                results = findMismatches(results, reference, v, compatible);
            }
        }
        for (var f : reference) {
            if (f instanceof FormatVariable v) {
                results = findMissing(results, v, candidate);
            }
        }
        return results;
    }

    private static Set<Incompatibility> findMismatches(Set<Incompatibility> results, FormatExpression ref,
                                                       FormatVariable candidate,
                                                       BiPredicate<FormatVariable, FormatVariable> compatible) {
        boolean found = false;
        for (var f : ref) {
            if (f instanceof FormatVariable v && v.index() == candidate.index()) {
                results = findMismatches(results, v, candidate, compatible);
                found = true;
            }
        }
        if (!found) {
            results = mutable(results);
            results.add(new Incompatibility(candidate.index(), Problem.NONEXISTENT));
        }
        return results;
    }

    private static Set<Incompatibility> findMismatches(Set<Incompatibility> results, FormatVariable ref,
                                                       FormatVariable candidate,
                                                       BiPredicate<FormatVariable, FormatVariable> compatible) {
        if (!compatible.test(ref, candidate)) {
            results = mutable(results);
            results.add(new Incompatibility(ref.index(), Problem.MISMATCH));
        }
        return results;
    }

    private static Set<Incompatibility> findMissing(Set<Incompatibility> results, FormatVariable ref,
                                                    FormatExpression candidate) {
        for (var f : candidate) {
            if (f instanceof FormatVariable v && v.index() == ref.index()) {
                return results;
            }
        }
        results = mutable(results);
        results.add(new Incompatibility(ref.index(), Problem.MISSING));
        return results;
    }

    private static <E> Set<E> mutable(Set<E> set) {
        return set.isEmpty()
                ? new HashSet<>()
                : set;
    }

    /**
     * The nature of the incompatibility.
     */
    public enum Problem {
        /** Variables types failed to match */
        MISMATCH,
        /** Variable in the reference missing from candidate */
        MISSING,
        /** Variable in candidate does not exist in reference */
        NONEXISTENT,
    }

    /**
     * Incompatibilities between {@link FormatExpression}s.
     */
    public static final class Incompatibility {
        private final int index;
        private final Problem problem;

        Incompatibility(int index, Problem problem) {
            this.index = index;
            this.problem = problem;
        }

        /**
         * Associated variable index.
         *
         * @return variable index
         */
        public int index() {
            return index;
        }

        /**
         * Nature of the problem.
         *
         * @return nature
         */
        public Problem problem() {
            return problem;
        }

        /**
         * Informational.
         *
         * @return human readable text
         */
        @Override
        public String toString() {
            return switch (problem) {
                case MISMATCH -> "variable {" + index + "} has compatibility issues";
                case MISSING -> "variable {" + index + "} missing";
                case NONEXISTENT -> "variable {" + index + "} does not exist";
            };
        }
    }
}
