/* amodeus - Copyright (c) 2018, ETH Zurich, Institute for Dynamic Systems and Control */
package ch.ethz.idsc.amodeus.net;

import org.matsim.api.core.v01.Coord;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.VectorQ;

public enum TensorCoords {
    ;
    public static Tensor toTensor(Coord coord) {
        return Tensors.vectorDouble(coord.getX(), coord.getY());
    }

    /** @param vector of length 2
     * @return
     * @throws Exception if given vector does not have length 2 */
    public static Coord toCoord(Tensor vector) {
        VectorQ.requireLength(vector, 2); // ensure that vector of length 2;
        return new Coord( //
                vector.Get(0).number().doubleValue(), //
                vector.Get(1).number().doubleValue());
    }
}
