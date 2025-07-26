from flask import Flask, request, jsonify
from fashion_clip.fashion_clip import FashionCLIP
import numpy as np
from PIL import Image
import requests
from io import BytesIO

app = Flask(__name__)
fclip = FashionCLIP('fashion-clip')

# Full list (same as your setup)
labels = [
    # Tops
    "tshirt", "shirt", "blouse", "tankTop", "cropTop", "poloShirt", "vestTop", "sweater", "sweatshirt", "hoodie",
    "cardigan", "jacket", "coat", "blazer", "kimono", "poncho", "cape", "tunic", "top", "wrapTop",

    # Bottoms
    "trousers", "jeans", "leggings", "jeggings", "cargoPants", "chinos", "shorts", "skirt",
    "miniSkirt", "midiSkirt", "maxiSkirt", "culottes",

    # Dresses & One-pieces
    "dress", "eveningGown", "cocktailDress", "sunDress", "maxiDress", "jumpsuit", "playsuit",
    "dungarees", "romper", "bodysuit", "garmentSet",

    # Underwear & Loungewear
    "bra", "briefs", "boxers", "underwearBottom", "underwearTights", "swimwearBottom", "bikiniTop",
    "swimsuit", "lingerie", "pyjamaSet",

    # Footwear
    "sneakers", "boots", "sandals", "heels", "wedges", "loafers", "ballerinas", "flipFlops",
    "mules", "clogs", "slippers", "otherShoe",

    # Accessories
    "bag", "backpack", "belt", "scarf", "gloves", "necklace", "bracelet", "earring", "ring",
    "watch", "hairAliceBand", "hatBeanie", "hatBrim", "capPeaked", "sunglasses", "otherAccessories",

    # Fabrics/Materials
    "denim", "silk", "cotton", "wool", "linen", "leather", "fleece", "corduroy", "velvet",
    "knitted", "mesh", "satin",

    # Patterns/Styles
    "striped", "checked", "floral", "polkaDot", "camouflage", "animalPrint", "ripped", "distressed", "frayed",
    "ruffles", "pleated", "embroidered", "tieDye", "ruched", "sequined",

    # Fit & Cut
    "oversized", "slimFit", "regularFit", "skinny", "highWaisted", "lowRise", "cropped", "baggy", "fitted", "flared", "tapered",

    # Occasions
    "formal", "casual", "party", "workwear", "beachwear", "sportswear", "loungewear", "sleepwear", "festival", "winter", "summer",

    # Gender & Age
    "menswear", "womenswear", "unisex", "kidswear", "babywear", "teen", "adult",

    # Footwear features
    "laceUp", "slipOn", "platform", "openToe", "closedToe", "ankleBoot", "kneeHigh", "stiletto", "chunky", "flat",

    # Misc Tags
    "vintage", "y2k", "minimalist", "streetwear", "boho", "goth", "grunge", "preppy", "edgy", "classy", "sporty", "luxury"
]


# Full general label list
brands = [
    # Luxury Brands
    "gucci", "prada", "louisVuitton", "balenciaga", "dior", "chanel", "burberry", "givenchy", "versace", "fendi",
    "hermès", "celine", "yvesSaintLaurent", "valentino", "tomFord", "alexanderMcQueen", "loewe", "bottegaVeneta", "offWhite",

    # Streetwear Brands
    "supreme", "stüssy", "palace", "fearOfGod", "aBathingApe", "essentials", "kith", "rhude",
    "antiSocialSocialClub", "noah", "patta", "dailyPaper", "amiri", "carharttWip",

    # Fast Fashion / High Street
    "zara", "h&m", "uniqlo", "shein", "prettyLittleThing", "fashionNova", "boohoo",
    "asos", "forever21", "mango", "bershka", "pullAndBear", "stradivarius", "topshop", "primark", "missguided",

    # Sneaker & Sportswear Brands
    "nike", "adidas", "puma", "reebok", "newBalance", "vans", "converse", "fila", "asics",
    "underArmour", "champion", "jordan", "yeezy", "sketchers",

    # Nigerian & African Brands
    "orangeCulture", "lisaFolawiyo", "andreaIyamah", "kennethIze", "makiOh", "maiAtafo",
    "nkwo", "davidTlale", "maxhosa", "richMnisi", "imadEduso", "fruche", "tokyoJames", "iamIsigo"
]

# Extract color labels from master label list
color_labels = [
    "red", "blue", "green", "black", "white", "yellow", "orange", "purple",
    "pink", "beige", "brown", "grey", "neon", "pastel",
    "whitewashed", "darkwash", "lightwash"
]



# Get embeddings
label_embeddings = fclip.encode_text(labels, batch_size=32)
label_embeddings = label_embeddings / np.linalg.norm(label_embeddings, ord=2, axis=-1, keepdims=True)

brand_embeddings = fclip.encode_text(brands, batch_size=32)
brand_embeddings = brand_embeddings / np.linalg.norm(brand_embeddings, ord=2, axis=-1, keepdims=True)

color_embeddings = fclip.encode_text(color_labels, batch_size=16)
color_embeddings = color_embeddings / np.linalg.norm(color_embeddings, ord=2, axis=-1, keepdims=True)


@app.route("/label", methods=["POST"])
def label_image():
    data = request.get_json()
    image_url = data.get("image_url")

    if not image_url:
        return jsonify({"error": "Missing image_url"}), 400

    try:
        # response = requests.get(image_url)
        image =  Image.open(image_url).convert("RGB")

        image_embedding = fclip.encode_images([image], batch_size=1)
        image_embedding = image_embedding / np.linalg.norm(image_embedding, ord=2, axis=-1, keepdims=True)

        # Main labels
        label_scores = label_embeddings.dot(image_embedding.T).squeeze()
        top_indices = np.argsort(label_scores)[::-1][:10]
        top_labels = [{"label": labels[i], "score": float(label_scores[i])} for i in top_indices]

        # Top brand
        brand_scores = brand_embeddings.dot(image_embedding.T).squeeze()
        best_brand_idx = int(np.argmax(brand_scores))
        top_labels.insert(0, {
            "label": brands[best_brand_idx],
            "score": float(brand_scores[best_brand_idx])
        })

        # Top 3 colors
        color_scores = color_embeddings.dot(image_embedding.T).squeeze()
        top_color_indices = np.argsort(color_scores)[::-1][:3]
        for i in top_color_indices:
            top_labels.insert(1, {
                "label": color_labels[i],
                "score": float(color_scores[i])
            })

        return jsonify(top_labels)

    except Exception as e:
        return jsonify({"error": str(e)}), 500

@app.route("/labels", methods=["GET"])
def get_labels():
    return jsonify({
        "labels": labels,
        "brands": brands,
        "colors": color_labels
    })


if __name__ == "__main__":
    app.run(debug=False)

