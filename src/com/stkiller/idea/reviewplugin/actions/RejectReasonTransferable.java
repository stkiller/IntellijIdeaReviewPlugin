package com.stkiller.idea.reviewplugin.actions;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import com.intellij.util.ArrayUtil;
import org.jetbrains.annotations.Nullable;

/**
 * @author andrei (10/12/2012)
 */
class RejectReasonTransferable implements Transferable {

    private final String fqn;


    public RejectReasonTransferable(final String fqn) {
        this.fqn = fqn;
    }


    public DataFlavor[] getTransferDataFlavors() {
        return new DataFlavor[]{DataFlavor.stringFlavor};
    }


    public boolean isDataFlavorSupported(final DataFlavor flavor) {
        return ArrayUtil.find(getTransferDataFlavors(), flavor) != -1;
    }


    @Nullable
    public Object getTransferData(final DataFlavor flavor) throws UnsupportedFlavorException, IOException {
        if (isDataFlavorSupported(flavor)) {
            return fqn;
        }
        return null;
    }
}
