package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "Adding a song."

    request {
        url "/songs"
        method POST()
        headers {
            contentType(applicationJson())
        }
        body(
                id: "1",
                name: "name",
                artist: "artist",
                album: "album",
                duration: "12:34",
                year: "2020"
        )
    }

    response {
        status 200
        headers {
            contentType(applicationJson())
        }
        body([id: 1L])
    }
}