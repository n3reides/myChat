package mychat;


interface ObjectStreamListener {

    public void objectReceived(int number, Object object, Exception exception);
    
}