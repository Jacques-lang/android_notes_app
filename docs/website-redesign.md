# Mental Health + Physical Health Coach Website Revamp

This document lays out a redesign direction that mimics Apple-style narrative scrolling: big visual panels, spacious typography, cinematic transitions, and minimal UI chrome. It includes an updated information architecture, layout guidance, motion/interaction ideas, and image-generation prompts you can use to create a consistent visual system.

## Goals
- **More premium + breathable**: reduce visual clutter with larger sections, tighter copy, and higher contrast between light/dark bands.
- **Narrative scrolling**: make each section feel like a “slide” with a single message and a crisp call-to-action.
- **Image-led storytelling**: introduce calm, high-quality visuals that reinforce safety, clarity, and progress.
- **Future AI coach positioning**: preview progress tracking without overpromising clinical care.

---

## IA (Information Architecture)

**Top-level pages**
1. **Home** (storytelling hero + product overview)
2. **Conditions** (Anxiety, Stress, Sleep, Focus, Burnout)
3. **Tools** (Breathing, Journaling, Grounding, Stretching)
4. **AI Coach (Coming Soon)**
5. **Resources** (non-clinical education + crisis support)

**Home page section flow**
1. **Hero** — “A calmer body. A lighter mind.” + 1 line subtext + primary CTA
2. **Problem → Relief** — 2–3 short cards describing common states and quick resets
3. **Signature Feature** — “2-minute reset” with animated breathing ring
4. **Visual Story** — full-bleed image panel with short headline
5. **Focus Topics** — 4–6 topic cards (Anxiety, Stress, Sleep, Focus)
6. **AI Coach preview** — small “coming soon” area with mock progress graph
7. **SOS / Safety** — gentle but clear safety banner

---

## Layout + Styling Principles (Apple-like)
- **One idea per section**: minimize stacked cards and long copy blocks.
- **High whitespace**: increase vertical padding between sections to create rhythm.
- **Large typography**: 48–64px hero headers, 20–24px body lead text.
- **Muted neutrals + soft gradients**: off-white backgrounds, soft peach + warm gray accents.
- **Elegant borders**: 1px lines with low-opacity and generous border radius.
- **Motion with restraint**: subtle parallax, fade/slide, and hover lift.

---

## Motion + Interaction (Framer Motion ready)
Use gentle movement to create a “slideshow” feel without overwhelming the user.

**Global**
- Staggered fade/slide of each section when in view.
- Large background gradient blobs that slowly drift.

**Hero**
- Type reveal (fade-up) + subtle moving highlight sweep.

**Signature Feature**
- Animated breathing ring (expand/contract) synced to CTA.

**Image Panels**
- Parallax image shift (2–4px) on scroll.

**Cards**
- Hover lift of 2–4px with soft shadow.

---

## Content Tone
- **Non-clinical + supportive**: avoid diagnosing language.
- **Actionable micro-steps**: “Do this for 2 minutes.”
- **Safety-forward**: always include SOS info in a calm but visible way.

---

## Image System (Use 4–6 core visuals)
Keep imagery consistent in style and palette. Suggested themes:

1. **Hero Calm Abstract**
2. **Breathing / Air / Flow**
3. **Grounding / Nature / Soft Light**
4. **Focus / Minimal Desk / Morning Sun**
5. **Sleep / Night Gradient / Moon Glow**
6. **AI Coach / Futuristic but warm**

### AI Image Prompts (copy/paste)
Use a **single style** to maintain a cohesive brand.

**Style guide**
- Soft gradients, warm neutrals, minimal detail
- Subtle bokeh, clean light
- Calm, premium, airy
- No faces; avoid medical imagery
- 16:9 or 3:2 ratio for hero panels

**Prompt 1 — Hero Calm Abstract**
> Soft gradient abstract landscape, warm peach and cream tones, airy light, minimal shapes, premium product aesthetic, high resolution, subtle bokeh, no text, no people

**Prompt 2 — Breathing / Air Flow**
> Gentle flowing translucent ribbons of light, soft white and pale amber palette, smooth curves, abstract motion blur, calming, minimal, premium aesthetic

**Prompt 3 — Grounding / Nature Light**
> Minimalist scene of soft sunlight through translucent leaves, warm beige and pale green, high-key lighting, shallow depth of field, peaceful, clean composition, no people

**Prompt 4 — Focus / Desk Calm**
> Minimal desk with a single notebook and cup, soft morning light, warm neutral palette, high-end product shot, clean shadows, no clutter, no people

**Prompt 5 — Sleep / Night Gradient**
> Abstract night gradient with moon glow, deep navy to soft lavender, minimal, dreamy, calm lighting, premium look

**Prompt 6 — AI Coach / Warm Futurism**
> Abstract futuristic interface glow, soft amber and slate gradients, gentle geometric shapes, premium tech aesthetic, calm, clean, no harsh neon

---

## Example Home Page Structure (pseudo)
```
Hero (H1 + CTA + hero image)
↓
Problem → Relief (3 cards)
↓
Signature Feature (breathing tool)
↓
Image Panel (full width)
↓
Topic Grid (4–6 topics)
↓
AI Coach Preview (coming soon)
↓
Resources + SOS
```

---

## Next Steps
1. Replace current busy sections with fewer, larger story bands.
2. Insert the image panels between text-heavy sections.
3. Use the prompts to generate 4–6 consistent images.
4. Migrate repeated cards into reusable components.

If you want, I can map these steps directly into your existing `Home.jsx` and `Anxiety.jsx` components in a follow-up, and provide image assets sized for your current design tokens.
