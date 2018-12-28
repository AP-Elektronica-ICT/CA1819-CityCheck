package cloudapplications.citycheck;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import cloudapplications.citycheck.APIService.NetworkManager;
import cloudapplications.citycheck.APIService.NetworkResponseListener;
import cloudapplications.citycheck.Models.Game;

import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class NetworkManagerTest {

    private NetworkResponseListener listener;


    @Captor
    private ArgumentCaptor<List<Game>> captor;

    @Before
    public void setup(){
        listener = mock(NetworkResponseListener.class);
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void createGamesTest(){
        NetworkManager.getInstance().createNewGame(new Game(1), new NetworkResponseListener<Game>() {
            @Override
            public void onResponseReceived(Game game) {

            }

            @Override
            public void onError() {

            }
        });
        captor.capture();
        captor.getValue().equals(Game.class);
    }

}
