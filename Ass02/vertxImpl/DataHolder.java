package vertxImpl;

import java.util.List;


final class DataHolder {
    private final List<NodeTuple> data;
    @Override
    public String toString() {
        return "Holder{" +
                "data=" + data +
                '}';
    }

    public DataHolder(final List<NodeTuple> data) {
        this.data = data;
    }

    public List<NodeTuple> getData() {
        return data;
    }
}
