package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    request {
        url "/songs/1"
        method GET()
    }
    response {
        status OK()
        headers {
            contentType('application/json')
        }
        body(
            id: 1,
            name: "A song",
            artist: "John Doe",
            album: "Songs",
            duration: "12:34",
            year: "2020"
        )
    }
}
