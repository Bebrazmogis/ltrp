package lt.ltrp.plugin.streamer;

/**
 * Created by Justas on 2015.06.07.
 */
public enum PlayerEditDynamicObjectResponse {

        ResponseCancel(0),
        ResponseFinal(1),
        ResponseUpdate(2);

        private int value;

        PlayerEditDynamicObjectResponse(int value) {
            this.value = value;
        }


}
