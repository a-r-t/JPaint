package Canvas;

public interface CanvasHistoryListener {
    void onHistorySizeChange(int performedSize, int recallSize);
    void onUndo();
    void onRedo();
}
