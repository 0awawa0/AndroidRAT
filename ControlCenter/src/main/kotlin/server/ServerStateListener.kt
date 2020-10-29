package server


interface ServerStateListener {

    fun onClientsListChanged(clients: List<Client>) {}
}