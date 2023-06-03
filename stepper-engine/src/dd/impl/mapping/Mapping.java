package dd.impl.mapping;

public class Mapping<A, B> {
        private A first;
        private B second;

        public Mapping(A first, B second) {
            this.first = first;
            this.second = second;
        }

        public A getFirst() {
            return first;
        }

        public B getSecond() {
            return second;
        }

        public void setFirst(A first) { this.first = first; }

        public void setSecond(B second) { this.second = second; }




    @Override
    public String toString() {
        return "car: " + first.toString() + "\n" +
                "cdr: " + second.toString();
    }
}
