package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    request {
        url "/songs"
        method POST()
        headers {
            contentType('application/json')
        }
        body(
            id: 1,
            name: "Song",
            artist: "John Doe",
            album: "Songs",
            duration: "12:34",
            year: "2020"
        )
    }
    response {
        status OK()
        headers {
            contentType('application/json')
        }
        body([id: 1])
    }
}
